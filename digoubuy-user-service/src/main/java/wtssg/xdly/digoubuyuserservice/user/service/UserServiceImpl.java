package wtssg.xdly.digoubuyuserservice.user.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import wtssg.xdly.digoubuyuserservice.common.constants.Constants;
import wtssg.xdly.digoubuyuserservice.common.exception.DigoubuyException;
import wtssg.xdly.digoubuyuserservice.common.utils.RandomNumberCodeUtil;
import wtssg.xdly.digoubuyuserservice.common.utils.ZkClient;
import wtssg.xdly.digoubuyuserservice.jms.KafkaSendService;
import wtssg.xdly.digoubuyuserservice.user.dao.UserMapper;
import wtssg.xdly.digoubuyuserservice.user.entity.User;
import wtssg.xdly.digoubuyuserservice.user.entity.UserElement;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("userService")
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private CuratorFramework curatorFramework;

    @Autowired
    private KafkaSendService kafkaSendService;

    @Override
    public UserElement login(User user) {
        UserElement ue = null;
        User existUser = userMapper.selectByMobile(user.getMobile());
        String verCode = (String) redisTemplate.opsForValue().get(user.getMobile());
        if (existUser == null || user.getVerCode() == null || !user.getVerCode().equals(verCode)) {
            throw new DigoubuyException("用户不存在");
        } else {
            boolean result = passwordEncoder.matches(user.getPassword(), existUser.getPassword());
            if (!result) {
                throw new DigoubuyException("密码错误");
            } else {
                // 验证通过 赋值ue
                ue = new UserElement(existUser);
            }
        }
        return ue;
    }

    @Override
    public void registerUser(User user) {
        InterProcessMutex lock = null;
        try {
            lock = new InterProcessMutex(curatorFramework, Constants.USER_REGISTER_DISTRIBUTE_LOCK_PATH);
            boolean retry = true;
            // 当获取锁失败，重新尝试获取锁。
            while (retry) {
                if (lock.acquire(3000, TimeUnit.MILLISECONDS)) {
                    // 获得了分布式锁，悲观锁
                    User repeatedUser = userMapper.selectByMobile(user.getMobile());
                    User repeatedNickname = userMapper.selectByNickname(user.getNickname());
                    if (repeatedUser != null) {
                        // 用户手机号重复
                        throw new DigoubuyException("用户手机号已存在");
                    } else if (repeatedNickname != null) {
                        // 用户昵称重复
                        throw new DigoubuyException("用户昵称已存在");
                    }
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    user.setNickname(user.getNickname());
                    user.setCreateTime(new Date());
                    user.setUpdateTime(new Date());
                    userMapper.insertSelective(user);
                    retry = false;
                }
            }
        } catch (Exception e) {
            log.error("用户注册异常");
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                    log.info(user.getEmail() + Thread.currentThread().getName() + "释放锁");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void sendVerCode(String mobile) {
        String verCode = RandomNumberCodeUtil.verCode();
        Map<String, String> smsParam = new HashMap<>();
        smsParam.put("mobile", mobile);
        smsParam.put("tplId", Constants.MDSMS_VERCODE_TPLID);
        smsParam.put("verCode", verCode);
        String message = JSON.toJSONString(smsParam);
        kafkaSendService.sendSms(message);
    }

}
