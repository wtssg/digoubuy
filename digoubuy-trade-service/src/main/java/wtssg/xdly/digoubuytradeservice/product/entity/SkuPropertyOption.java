package wtssg.xdly.digoubuytradeservice.product.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SkuPropertyOption implements Serializable {

    private Long id;

    private Long skuId;

    private Long propertyId;

    private Long propertyOptionId;

    private Byte enableFlag;

    private transient Date createTime;

    private transient Date updateTime;

}