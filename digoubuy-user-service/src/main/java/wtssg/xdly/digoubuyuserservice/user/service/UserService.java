package wtssg.xdly.digoubuyuserservice.user.service;

import org.springframework.stereotype.Service;
import wtssg.xdly.digoubuyuserservice.user.entity.User;
import wtssg.xdly.digoubuyuserservice.user.entity.UserElement;


public interface UserService {
    UserElement login(User user);

    void registerUser(User user) throws Exception;

    public void sendVerCode(String mobile);
}
