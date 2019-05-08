package wtssg.xdly.digoubuytradeservice.trade.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ShoppingCart {
    private Long id;

    private Long userUuid;

    private Long skuId;

    private Integer skuNum;

    private Byte checkStatus;

    private Date createTime;

    private Date updateTime;

}