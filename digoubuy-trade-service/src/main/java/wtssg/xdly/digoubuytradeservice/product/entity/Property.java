package wtssg.xdly.digoubuytradeservice.product.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Property implements Serializable {

    private Long id;

    private Long categoryId;

    private String propertyName;

    private transient Date createTime;

    private transient Date updateTime;

}