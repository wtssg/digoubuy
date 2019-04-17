package wtssg.xdly.digoubuyuserservice.user.controller;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import wtssg.xdly.digoubuyuserservice.common.constants.Constants;
import wtssg.xdly.digoubuyuserservice.common.resp.ApiResult;
import wtssg.xdly.digoubuyuserservice.common.utils.RandomNumberCodeUtil;
import wtssg.xdly.digoubuyuserservice.jms.KafkaSendService;
import wtssg.xdly.digoubuyuserservice.user.entity.User;
import wtssg.xdly.digoubuyuserservice.user.entity.UserElement;
import wtssg.xdly.digoubuyuserservice.user.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;


    @RequestMapping(value = "/login")
    public ApiResult login(@RequestBody @Valid User user, HttpSession session) {
        ApiResult<UserElement> result = new ApiResult<>(Constants.RESP_STATUS_OK, "登录成功");
        UserElement ue = userService.login(user);
        if (ue != null) {
            if (session.getAttribute(Constants.REQUEST_USER_SESSION) == null) {
                // 存session
                session.setAttribute(Constants.REQUEST_USER_SESSION,ue);
            }
            result.setData(ue);
        }
        return result;
    }

    @RequestMapping("/register")
    public ApiResult register(@RequestBody @Valid User user) throws Exception {
        userService.registerUser(user);
        return new ApiResult<>(Constants.RESP_STATUS_OK, "注册成功");
    }

    @RequestMapping("/sendVerCode/{mobile}")
    public ApiResult sendVerCode(@PathVariable("mobile") String mobile) {
        userService.sendVerCode(mobile);
        return new ApiResult<>(Constants.RESP_STATUS_OK, "验证码发送完成");
    }

}
