package wtssg.xdly.digoubuytradeservice.trade.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import wtssg.xdly.digoubuytradeservice.common.resp.ApiResult;
import wtssg.xdly.digoubuytradeservice.trade.entity.StockReduce;

import java.util.List;
import java.util.Map;

@FeignClient(name = "stock-service", fallback = StockServiceFallBack.class)
public interface StockServiceClient {

    @RequestMapping(value = "/stock/reduce", method = RequestMethod.POST)
    ApiResult<Map<Long, Integer>> reduceStock(@RequestBody List<StockReduce> stockReduceList);
}
