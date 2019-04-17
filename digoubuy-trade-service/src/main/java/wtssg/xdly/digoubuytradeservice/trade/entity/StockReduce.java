package wtssg.xdly.digoubuytradeservice.trade.entity;

import lombok.Data;

@Data
public class StockReduce {

    private Long orderNo;

    private Long skuId;

    private Integer reduceCount;

}
