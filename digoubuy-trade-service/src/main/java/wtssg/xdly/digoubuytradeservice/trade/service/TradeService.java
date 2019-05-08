package wtssg.xdly.digoubuytradeservice.trade.service;

import org.springframework.transaction.annotation.Transactional;
import wtssg.xdly.digoubuytradeservice.product.entity.ProductSku;
import wtssg.xdly.digoubuytradeservice.trade.entity.ShoppingCart;
import wtssg.xdly.digoubuytradeservice.trade.entity.ShoppingCartItem;
import wtssg.xdly.digoubuytradeservice.trade.entity.Trade;
import wtssg.xdly.digoubuytradeservice.trade.entity.TradeItem;

import java.math.BigDecimal;
import java.util.List;

public interface TradeService {

    void putIntoCart(ShoppingCartItem sku, long uuid);

    void changeCartItemNum(ShoppingCart shoppingCart);

    void delCartItems(List<Long> idList);

    List<ShoppingCartItem> getCartItem(long uuid, boolean selected);

    Trade createOrderByCart(Long addressId, Long uuid);

    TradeItem createOrderByOne(Long addressId, Long uuid);

    void createTempOrder(TradeItem item);

    TradeItem getTempOrder(Long uuid);

    Trade getTrade(Long tradeNo, Long uuid);

    void payOrder(Long out_trade_no, BigDecimal payment);

    List<Trade> getAllTrade(Long uuid);
}
