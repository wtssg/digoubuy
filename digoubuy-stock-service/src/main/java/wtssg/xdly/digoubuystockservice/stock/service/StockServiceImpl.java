package wtssg.xdly.digoubuystockservice.stock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wtssg.xdly.digoubuystockservice.common.constants.Constants;
import wtssg.xdly.digoubuystockservice.common.utils.RedisUtils;
import wtssg.xdly.digoubuystockservice.stock.dao.StockFlowMapper;
import wtssg.xdly.digoubuystockservice.stock.dao.StockMapper;
import wtssg.xdly.digoubuystockservice.stock.entity.Stock;
import wtssg.xdly.digoubuystockservice.stock.entity.StockFlow;
import wtssg.xdly.digoubuystockservice.stock.entity.StockReduce;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service("stockServiceImpl")
public class StockServiceImpl implements StockService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private StockFlowMapper stockFlowMapper;


    /**
     * 查询单个商品的库存，如果在redis中不存在则先存入redis
     * @param skuId 商品id
     * @return 商品库存信息
     */
    @Override
    @Transactional
    public int queryStock(long skuId) {

        Stock stock;
        String stockKey = Constants.CACHE_PRODUCT_STOCK + ":" + skuId;
        String lockStockKey = Constants.CACHE_PRODUCT_STOCK_LOCK + ":" + skuId;

        // 先尝试从redis获得数据
        Object stockObj = redisTemplate.opsForValue().get(stockKey);
        Integer stockInRedis = null;
        if (stockObj != null) {
            stockInRedis = Integer.parseInt(String.valueOf(stockObj));
        }
        if (stockInRedis == null) {
            // 数据在redis中不存在，从数据库中查出，并存到redis中
            stock = stockMapper.selectBySkuId(skuId);
            String stockValue = String.valueOf(stock.getStock());
            String lockStockValue = String.valueOf(stock.getLockStock());
            redisUtils.skuStockInit(stockKey, lockStockKey, stockValue, lockStockValue);
        } else {
            // 数据在redis中存在
            return stockInRedis;
        }
        return stock.getStock();
    }

    /**
     * 多个商品在redis中扣减库存，并在mysql中记录流水
     * @param stockReduceList 库存扣减列表
     * @return 每个商品的库存扣减情况，使用map存储，value为-1时失败，1时成功
     */
    @Override
    @Transactional
    public Map<Long, Integer> reduceStock(List<StockReduce> stockReduceList) {

        Long orderNo = stockReduceList.get(0).getOrderNo();
        // 为了幂等（多次提交统一订单的结果相同，即库存只会扣一次），使用分布式锁（redis实现的分布式锁）
        boolean lockResult = redisUtils.distributeLock(Constants.ORDER_RETRY_LOCK +
                orderNo.toString(), orderNo.toString(), 300000);
        Map<Long, Integer> resultMap = new HashMap<>(stockReduceList.size());

        for (StockReduce param : stockReduceList) {

            String stockKey = Constants.CACHE_PRODUCT_STOCK+":"+param.getSkuId();
            String stockLockKey = Constants.CACHE_PRODUCT_STOCK_LOCK+":"+param.getSkuId();
            Object result = redisUtils.reduceStock(stockKey,stockLockKey,param.getReduceCount().toString(),
                    String.valueOf(Math.abs(param.getReduceCount())));
            if (result instanceof Long) {
                // 库存不足，不存在，下单失败
                resultMap.put(param.getSkuId(), -1);
            } else if (result instanceof List) {
                // 扣单成功记录流水
                List resultList = ((List) result);
                int stockAftChange = ((Long)resultList.get(0)).intValue();
                int stockLockAftChange = ((Long) resultList.get(1)).intValue();
                StockFlow stockFlow = new StockFlow();
                stockFlow.setOrderNo(param.getOrderNo());
                stockFlow.setSkuId(param.getSkuId());
                stockFlow.setLockStockAfter(stockLockAftChange);
                stockFlow.setLockStockBefore(stockLockAftChange+param.getReduceCount());
                stockFlow.setLockStockChange(Math.abs(param.getReduceCount()));
                stockFlow.setStockAfter(stockAftChange);
                stockFlow.setStockBefore(stockAftChange+Math.abs(param.getReduceCount()));
                stockFlow.setStockChange(param.getReduceCount());
                stockFlowMapper.insertSelective(stockFlow);
                resultMap.put(param.getSkuId(),1);
            }
        }
        return resultMap;
    }
}
