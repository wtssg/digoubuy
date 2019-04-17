package wtssg.xdly.digoubuystockservice.stock.service;

import wtssg.xdly.digoubuystockservice.stock.entity.StockReduce;

import java.util.List;
import java.util.Map;

public interface StockService {
    int queryStock(long skuId);

    Map<Long, Integer> reduceStock(List<StockReduce> stockReduceList);
}
