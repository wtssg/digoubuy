package wtssg.xdly.digoubuyuserservice.user.entity;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class User {

    private Long id;

    private Long uuid;

    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String nickname;

    private String mobile;

    private Date createTime;

    private Date updateTime;

    private String verCode;

}