package wtssg.xdly.digoubuystockservice.stock.entity;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 前端传来的增删sku库存的对象
 */
@Data
public class StockReduce {

    private Long orderNo;

    private Long skuId;

    private Integer reduceCount;

    public static void main(String[] args) {
        List<StockReduce> list = new ArrayList<>();
        StockReduce stockReduce = new StockReduce();
        stockReduce.setReduceCount(1);
        stockReduce.setSkuId(16L);
        list.add(stockReduce);
        System.out.println(JSON.toJSON(list));
    }

}
