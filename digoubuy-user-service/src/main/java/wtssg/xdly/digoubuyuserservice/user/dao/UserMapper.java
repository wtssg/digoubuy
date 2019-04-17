package wtssg.xdly.digoubuyuserservice.user.dao;

import org.apache.ibatis.annotations.Mapper;
import wtssg.xdly.digoubuyuserservice.user.entity.User;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByEmail(String email);

    User selectByMobile(String mobile);


    User selectByNickname(String nickname);
}