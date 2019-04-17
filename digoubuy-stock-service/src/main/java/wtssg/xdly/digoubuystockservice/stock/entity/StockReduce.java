package wtssg.xdly.digoubuystockservice.stock.entity;

import lombok.Data;

/**
 * 前端传来的增删sku库存的对象
 */
@Data
public class StockReduce {

    private Long orderNo;

    private Long skuId;

    private Integer reduceCount;

}
