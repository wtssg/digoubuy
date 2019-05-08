package wtssg.xdly.digoubuytradeservice.product.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserElement implements Serializable {

    private Long id;

    private Long uuid;

    private String mobile;

    private String nickname;

    public UserElement(){};

    public UserElement(User user) {
        this.id = user.getId();
        this.uuid = user.getUuid();
        this.mobile = user.getMobile();
        this.nickname = user.getNickname();
    }
}
