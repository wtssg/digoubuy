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
import wtssg.xdly.digoubuyuserservice.user.entity.UserAddress;
import wtssg.xdly.digoubuyuserservice.user.entity.UserElement;
import wtssg.xdly.digoubuyuserservice.user.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
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
                session.setAttribute(Constants.REQUEST_USER_SESSION,ue.getUuid());
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

    @RequestMapping("/verVerCode/{mobile}/{verCode}")
    public ApiResult sendVerCode(@PathVariable("mobile") String mobile, @PathVariable("verCode") String verCode) {
        if (userService.verVerCode(mobile, verCode)) {
            return new ApiResult<>(Constants.RESP_STATUS_OK, "验证码正确");
        } else {
            return new ApiResult<>(Constants.RESP_STATUS_OK, "验证码错误");
        }
    }

    @RequestMapping("/addUserAddress")
    public ApiResult<Long> addUserAddress(@RequestBody UserAddress userAddress, HttpSession session) {
        ApiResult<Long> result = new ApiResult<>(Constants.RESP_STATUS_OK, "添加收货地址完成");
        userAddress.setUuid((Long) session.getAttribute(Constants.REQUEST_USER_SESSION));
        userService.addUserAddress(userAddress);
        result.setData(userAddress.getId());
        return result;
    }

    @RequestMapping("/remUserAddress/{id}")
    public ApiResult remUserAddress(@PathVariable("id") Long id) {
        userService.remUserAddress(id);
        return new ApiResult<>(Constants.RESP_STATUS_OK, "删除收货地址完成");
    }

    @RequestMapping("/getUserAddressList")
    public ApiResult<List<UserAddress>> getUserAddressList(HttpSession session) {
        ApiResult<List<UserAddress>> result = new ApiResult<>(Constants.RESP_STATUS_OK, "获得收货地址完成");
        List<UserAddress> addressList =  userService.getUserAddressList((Long) session.getAttribute(Constants.REQUEST_USER_SESSION));
        result.setData(addressList);
        return result;
    }


    @RequestMapping("/setDefaultAddress/{id1}/{id2}")
    public ApiResult setDefaultAddress(@PathVariable("id1") Long id1, @PathVariable("id2") Long id2) {
        userService.setDefaultAddress(id1, id2);
        return new ApiResult<>(Constants.RESP_STATUS_OK, "设置默认收货地址完成");
    }

    @RequestMapping("/updateUserAddress")
    public ApiResult updateUserAddress(@RequestBody UserAddress userAddress, HttpSession session) {
        userAddress.setUuid((Long) session.getAttribute(Constants.REQUEST_USER_SESSION));
        ApiResult<Long> result = new ApiResult<>(Constants.RESP_STATUS_OK, "更新收货地址完成");
        userService.updateUserAddress(userAddress);
        result.setData(userAddress.getId());
        return result;
    }

    @RequestMapping("/getAddressById/{id}")
    public ApiResult<UserAddress> getAddressById(@PathVariable("id") Long id) {
        ApiResult<UserAddress> result = new ApiResult<>(Constants.RESP_STATUS_OK, "获得订单地址完成");
        UserAddress userAddress = userService.getUserAddress(id);
        result.setData(userAddress);
        return result;
    }
}
