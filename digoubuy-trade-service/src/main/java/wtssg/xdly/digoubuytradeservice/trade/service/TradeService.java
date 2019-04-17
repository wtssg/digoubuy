package wtssg.xdly.digoubuytradeservice.trade.service;

import wtssg.xdly.digoubuytradeservice.trade.entity.TradeItem;

import java.util.List;

public interface TradeService {
    List<TradeItem> createOrder(List<TradeItem> tradeItemList);

    void payOrder(String out_trade_no);
}
