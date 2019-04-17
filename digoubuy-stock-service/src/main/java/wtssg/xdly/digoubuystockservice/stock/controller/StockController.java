package wtssg.xdly.digoubuystockservice.stock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wtssg.xdly.digoubuystockservice.common.constants.Constants;
import wtssg.xdly.digoubuystockservice.common.resp.ApiResult;
import wtssg.xdly.digoubuystockservice.stock.entity.Stock;
import wtssg.xdly.digoubuystockservice.stock.entity.StockReduce;
import wtssg.xdly.digoubuystockservice.stock.service.StockService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("stock")
public class StockController {

    @Autowired
    @Qualifier("stockServiceImpl")
    private StockService stockService;


    /**
     * 查询单个商品的库存，如果在redis中不存在则先存入redis
     * @param skuId 商品id
     * @return 商品库存信息
     */
    @RequestMapping("/query/{skuId}")
    public ApiResult<Stock> queryStock(@PathVariable long skuId) {

        ApiResult<Stock> result = new ApiResult<>(Constants.RESP_STATUS_OK, "查询商品成功");

        Stock stock = new Stock();
        stock.setSkuId(skuId);

        int stockCount = stockService.queryStock(skuId);
        stock.setStock(stockCount);

        result.setData(stock);
        return result;
    }

    /**
     * 多个商品库存扣减
     * @param stockReduceList 商品扣减信息列表
     * @return ApiResult
     */
    @RequestMapping("/reduce")
    public ApiResult<Map<Long,Integer>> reduceStock(@RequestBody List<StockReduce> stockReduceList) {
        ApiResult<Map<Long,Integer>> result = new ApiResult<>(Constants.RESP_STATUS_OK, "库存扣减成功");
        Map<Long,Integer> resultMap = stockService.reduceStock(stockReduceList);
        result.setData(resultMap);
        return result;
    }
}
