package wtssg.xdly.digoubuytradeservice.trade.feign;

import wtssg.xdly.digoubuytradeservice.common.resp.ApiResult;
import wtssg.xdly.digoubuytradeservice.trade.entity.StockReduce;

import java.util.List;
import java.util.Map;

public class StockServiceFallBack implements StockServiceClient {
    @Override
    public ApiResult<Map<Long, Integer>> reduceStock(List<StockReduce> stockReduceList) {
        return null;
    }
}
