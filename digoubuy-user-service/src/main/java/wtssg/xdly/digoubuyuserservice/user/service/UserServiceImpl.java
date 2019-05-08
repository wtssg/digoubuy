package wtssg.xdly.digoubuyuserservice.user.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wtssg.xdly.digoubuyuserservice.common.constants.Constants;
import wtssg.xdly.digoubuyuserservice.common.exception.DigoubuyException;
import wtssg.xdly.digoubuyuserservice.common.utils.RandomNumberCodeUtil;
import wtssg.xdly.digoubuyuserservice.jms.KafkaSendService;
import wtssg.xdly.digoubuyuserservice.user.dao.UserAddressMapper;
import wtssg.xdly.digoubuyuserservice.user.dao.UserMapper;
import wtssg.xdly.digoubuyuserservice.user.entity.User;
import wtssg.xdly.digoubuyuserservice.user.entity.UserAddress;
import wtssg.xdly.digoubuyuserservice.user.entity.UserElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("userService")
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private CuratorFramework curatorFramework;

    @Autowired
    private KafkaSendService kafkaSendService;

    @Override
    public UserElement login(User user) {
        UserElement ue = null;
        User existUser = userMapper.selectByMobile(user.getMobile());
        String verCode = (String) redisTemplate.opsForValue().get(Constants.AUTH_CODE + user.getMobile());
        if (existUser == null) {
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
    @Transactional
    public void registerUser(User user) {
        InterProcessMutex lock = null;
        try {
            lock = new InterProcessMutex(curatorFramework, Constants.USER_REGISTER_DISTRIBUTE_LOCK_PATH);
            boolean retry = true;
            // 当获取锁失败，重新尝试获取锁。
            while (retry) {
                if (lock.acquire(3000, TimeUnit.MILLISECONDS)) {
                    // 获得了分布式锁，悲观锁
                    User repeatedUser = userMapper.selectByMobile( user.getMobile());
                    if (repeatedUser != null) {
                        // 用户手机号重复
                        throw new DigoubuyException("用户手机号已存在");
                    }
                    String key = Constants.AUTH_CODE + user.getMobile();
                    String verCode = (String) redisTemplate.opsForValue().get(key);
                    if (verCode == null || !verCode.equals(user.getVerCode())) {
                        throw new DigoubuyException("验证码错误");
                    }
                    User repeatedNickname = userMapper.selectByNickname(user.getNickname());
                    if (repeatedNickname != null) {
                        // 用户昵称重复
                        throw new DigoubuyException("用户昵称已存在");
                    }
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    user.setNickname(user.getNickname());
                    userMapper.insertSelective(user);
                    retry = false;
                }
            }
        } catch (Exception e) {
            log.error("用户注册异常");
            throw new DigoubuyException(e.getMessage());
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

    /**
     * 发送验证码
     * @param mobile
     */
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

    /**
     * 验证验证码是否正确
     * @return
     */
    @Override
    public  boolean verVerCode(String mobile, String verCode) {
        String key = Constants.AUTH_CODE + mobile;
        String code = (String) redisTemplate.opsForValue().get(key);
        return verCode.equals(code);
    }


    @Override
    public void addUserAddress(UserAddress userAddress) {
        userAddressMapper.insertSelective(userAddress);
    }

    @Override
    public void remUserAddress(Long id) {
        UserAddress userAddress = new UserAddress();
        userAddress.setId(id);
        userAddress.setDefaultFlag((byte) 1);
        userAddress.setStatus((byte) 0);
        userAddressMapper.updateByPrimaryKeySelective(userAddress);
    }

    @Override
    public List<UserAddress> getUserAddressList(Long uuid) {
        return userAddressMapper.selectByUuid(uuid);
    }

    @Override
    public UserAddress getUserAddress(Long id) {
        return userAddressMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long id1, Long id2) {
        UserAddress userAddress = new UserAddress();
        userAddress.setId(id1);
        userAddress.setDefaultFlag((byte) 0);
        userAddressMapper.updateByPrimaryKeySelective(userAddress);
        userAddress.setId(id2);
        userAddress.setDefaultFlag((byte) 1);
        userAddressMapper.updateByPrimaryKeySelective(userAddress);
    }

    @Override
    @Transactional
    public void updateUserAddress(UserAddress userAddress) {
        UserAddress address = new UserAddress();
        address.setId(userAddress.getId());
        address.setStatus((byte) 0);
        userAddressMapper.updateByPrimaryKeySelective(address);
        userAddress.setId(null);
        userAddressMapper.insertSelective(userAddress);
    }
}
