package wtssg.xdly.digoubuytradeservice.trade.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class TradeItem implements Serializable {

    private Long id;

    private Long userUuid;

    private Long tradeNo;

    private String tradeNoString;

    private Long spuId;

    private Long skuId;

    private String skuName;

    private String skuImageUrl;

    private BigDecimal currentPrice;

    private Integer quantity;

    private BigDecimal totalPrice;

    private Long addressId;

    private Date createTime;

    private Date updateTime;

}