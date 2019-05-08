package wtssg.xdly.digoubuytradeservice.product.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Property implements Serializable {
    private Long id;

    private String propertyName;

    private Date createTime;

    private Date updateTime;

}