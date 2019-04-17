package wtssg.xdly.digoubuytradeservice.trade.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wtssg.xdly.digoubuytradeservice.common.constants.Constants;
import wtssg.xdly.digoubuytradeservice.common.resp.ApiResult;
import wtssg.xdly.digoubuytradeservice.trade.entity.TradeItem;
import wtssg.xdly.digoubuytradeservice.trade.feign.KeyGeneratorServiceClient;
import wtssg.xdly.digoubuytradeservice.trade.service.TradeService;

import java.util.List;

@RestController
@RequestMapping("trade")
public class TradeController {

    @Autowired
    @Qualifier("tradeServiceImpl")
    private TradeService tradeService;



    @RequestMapping("/order")
    public ApiResult<List<TradeItem>> createOrder(@RequestBody List<TradeItem> tradeItemList) {

        ApiResult<List<TradeItem>> result = new ApiResult<>(Constants.RESP_STATUS_OK, "创建订单从成功");

        List<TradeItem> tradeItemsSuccResult = tradeService.createOrder(tradeItemList);
        result.setData(tradeItemsSuccResult);
        return result;
    }
}
