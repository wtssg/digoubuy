package wtssg.xdly.digoubuytradeservice.trade.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class Trade {
    private Long id;

    private Long tradeNo;

    private String tradeNoString;

    private Long userUuid;

    private Long addressId;

    private BigDecimal totalPrice;

    private BigDecimal payment;

    private Byte paymenyType;

    private Byte status;

    private Date paymentTime;

    private Date closeTime;

    private Date createTime;

    private Date updateTime;

    private Long seconds;

    private List<TradeItem> tradeItemList;
}