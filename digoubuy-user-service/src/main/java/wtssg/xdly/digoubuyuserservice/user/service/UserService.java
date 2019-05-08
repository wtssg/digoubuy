package wtssg.xdly.digoubuyuserservice.user.service;

import org.springframework.stereotype.Service;
import wtssg.xdly.digoubuyuserservice.user.entity.User;
import wtssg.xdly.digoubuyuserservice.user.entity.UserAddress;
import wtssg.xdly.digoubuyuserservice.user.entity.UserElement;

import java.util.List;


public interface UserService {
    UserElement login(User user);

    void registerUser(User user) throws Exception;

    public void sendVerCode(String mobile);

    public boolean verVerCode(String mobile, String verCode);

    public void addUserAddress(UserAddress userAddress);

    public void remUserAddress(Long id);

    public List<UserAddress> getUserAddressList(Long uuid);

    UserAddress getUserAddress(Long id);

    public void setDefaultAddress(Long id1, Long id2);

    public void updateUserAddress(UserAddress userAddress);
}
