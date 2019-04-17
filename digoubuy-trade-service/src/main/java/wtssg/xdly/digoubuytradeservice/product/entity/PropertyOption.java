package wtssg.xdly.digoubuytradeservice.product.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PropertyOption implements Serializable {

    private Long id;

    private Long propertyId;

    private String optionName;

    private transient Date createTime;

    private transient Date updateTime;

}