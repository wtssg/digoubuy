package wtssg.xdly.digoubuytradeservice.trade.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wtssg.xdly.digoubuytradeservice.common.constants.Constants;
import wtssg.xdly.digoubuytradeservice.common.exception.DigoubuyException;
import wtssg.xdly.digoubuytradeservice.common.resp.ApiResult;
import wtssg.xdly.digoubuytradeservice.product.entity.ProductSku;
import wtssg.xdly.digoubuytradeservice.trade.entity.ShoppingCart;
import wtssg.xdly.digoubuytradeservice.trade.entity.ShoppingCartItem;
import wtssg.xdly.digoubuytradeservice.trade.entity.Trade;
import wtssg.xdly.digoubuytradeservice.trade.entity.TradeItem;
import wtssg.xdly.digoubuytradeservice.trade.feign.KeyGeneratorServiceClient;
import wtssg.xdly.digoubuytradeservice.trade.service.TradeService;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("trade")
public class TradeController {

    @Autowired
    @Qualifier("tradeServiceImpl")
    private TradeService tradeService;


    @RequestMapping("/putIntoCart")
    public ApiResult putIntoCart(@RequestBody ShoppingCartItem cartItem, HttpSession session) {
        tradeService.putIntoCart(cartItem, (Long) session.getAttribute(Constants.REQUEST_USER_SESSION));
        return new ApiResult<>(Constants.RESP_STATUS_OK, "加入购物车成功");
    }

    @RequestMapping("/getCartItem")
    public ApiResult<List<ShoppingCartItem>> getCartItem(HttpSession session) {
        List<ShoppingCartItem> shoppingCartItemList =  tradeService.getCartItem((Long) session.getAttribute(Constants.REQUEST_USER_SESSION), false);
        ApiResult<List<ShoppingCartItem>> result = new ApiResult<>(Constants.RESP_STATUS_OK, "购物车获取成功");
        result.setData(shoppingCartItemList);
        return result;
    }

    @RequestMapping("/getCartItemSelected")
    public ApiResult<List<ShoppingCartItem>> getCartItemSelected(HttpSession session) {
        List<ShoppingCartItem> shoppingCartItemList =  tradeService.getCartItem((Long) session.getAttribute(Constants.REQUEST_USER_SESSION), true);
        ApiResult<List<ShoppingCartItem>> result = new ApiResult<>(Constants.RESP_STATUS_OK, "购物车获取成功");
        result.setData(shoppingCartItemList);
        return result;
    }

    @RequestMapping("/changeCartItem")
    public ApiResult putIntoCart(@RequestBody ShoppingCart shoppingCart) {
        tradeService.changeCartItemNum(shoppingCart);
        return new ApiResult<>(Constants.RESP_STATUS_OK, "修改购物车商品成功");
    }

    @RequestMapping("/delCartItems/{idString}")
    public ApiResult delCartItems(@PathVariable("idString") String idString) {
        String[] sa = idString.split("&");
        List<Long> idList = new ArrayList<>();
        for (String aSa : sa) {
            idList.add(Long.valueOf(aSa));
        }
        tradeService.delCartItems(idList);
        return new ApiResult<>(Constants.RESP_STATUS_OK, "删除购物车商品成功");
    }

    @RequestMapping("/orderByCart/{addressId}")
    public ApiResult<Trade> createOrderByCart(@PathVariable("addressId") Long addressId, HttpSession session) {

        ApiResult<Trade> result = new ApiResult<>(Constants.RESP_STATUS_OK, "创建订单从成功");

        Trade trade = tradeService.createOrderByCart(addressId, (Long) session.getAttribute(Constants.REQUEST_USER_SESSION));
        result.setData(trade);
        return result;
    }

    @RequestMapping("/orderByOne/{addressId}")
    public ApiResult<TradeItem> createOrderByOne(@PathVariable("addressId") Long addressId, HttpSession session){
        ApiResult<TradeItem> result = new ApiResult<>(Constants.RESP_STATUS_OK, "创建订单从成功");
        TradeItem resItem = tradeService.createOrderByOne(addressId, (Long) session.getAttribute(Constants.REQUEST_USER_SESSION));
        result.setData(resItem);
        return result;
    }

    @RequestMapping("/createTempOrder")
    public ApiResult createTempOrder(@RequestBody TradeItem item, HttpSession session) {
        item.setUserUuid((Long) session.getAttribute(Constants.REQUEST_USER_SESSION));
        tradeService.createTempOrder(item);
        return new ApiResult<>(Constants.RESP_STATUS_OK, "临时订单生成成功");
    }

    @RequestMapping("/getTempOrder")
    public ApiResult<TradeItem> getTempOrder(HttpSession session){
        ApiResult<TradeItem> result = new ApiResult<>(Constants.RESP_STATUS_OK, "临时订单获取成功");
        Long uuid = (Long) session.getAttribute(Constants.REQUEST_USER_SESSION);
        TradeItem resItem = tradeService.getTempOrder(uuid);
        result.setData(resItem);
        return result;
    }

    @RequestMapping("/getTrade/{tradeNo}")
    public ApiResult<Trade> getTrade(@PathVariable("tradeNo") Long tradeNo, HttpSession session) {
        ApiResult<Trade> result = new ApiResult<>(Constants.RESP_STATUS_OK, "订单获取成功");
        Long uuid = (Long) session.getAttribute(Constants.REQUEST_USER_SESSION);
        Trade trade = tradeService.getTrade(tradeNo, uuid);
        result.setData(trade);
        return result;
    }

    @RequestMapping("/getAllTrade")
    public ApiResult<List<Trade>> getAllTrade(HttpSession session) {
        ApiResult<List<Trade>> result = new ApiResult<>(Constants.RESP_STATUS_OK, "订单获取成功");
        Long uuid = (Long) session.getAttribute(Constants.REQUEST_USER_SESSION);
        List<Trade> tradeList = tradeService.getAllTrade(uuid);
        result.setData(tradeList);
        return result;
    }

}
