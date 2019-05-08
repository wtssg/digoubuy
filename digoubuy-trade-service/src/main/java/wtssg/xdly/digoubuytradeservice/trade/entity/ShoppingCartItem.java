package wtssg.xdly.digoubuytradeservice.trade.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShoppingCartItem {

    private Long id;

    private Long spuId;

    private Long skuId;

    private String skuName;

    private BigDecimal skuPrice;

    private String imgUrl;

    private Integer skuNum;

    private Byte checkStatus;

}
