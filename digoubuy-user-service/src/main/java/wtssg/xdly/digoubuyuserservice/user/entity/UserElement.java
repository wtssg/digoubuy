package wtssg.xdly.digoubuyuserservice.user.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserElement implements Serializable {

    private Long id;

    private Long uuid;

    private String email;

    private String nickname;

    public UserElement(){};

    public UserElement(User user) {
        this.id = user.getId();
        this.uuid = user.getUuid();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
    }
}
