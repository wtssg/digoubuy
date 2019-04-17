package wtssg.xdly.digoubuytradeservice.trade.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wtssg.xdly.digoubuytradeservice.common.enums.TradeStatus;
import wtssg.xdly.digoubuytradeservice.common.resp.ApiResult;
import wtssg.xdly.digoubuytradeservice.product.dao.ProductSkuMapper;
import wtssg.xdly.digoubuytradeservice.product.entity.ProductSku;
import wtssg.xdly.digoubuytradeservice.trade.dao.MessageEventPublishMapper;
import wtssg.xdly.digoubuytradeservice.trade.dao.TradeItemMapper;
import wtssg.xdly.digoubuytradeservice.trade.dao.TradeMapper;
import wtssg.xdly.digoubuytradeservice.trade.entity.MessageEventPublish;
import wtssg.xdly.digoubuytradeservice.trade.entity.StockReduce;
import wtssg.xdly.digoubuytradeservice.trade.entity.Trade;
import wtssg.xdly.digoubuytradeservice.trade.entity.TradeItem;
import wtssg.xdly.digoubuytradeservice.trade.feign.KeyGeneratorServiceClient;
import wtssg.xdly.digoubuytradeservice.trade.feign.StockServiceClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("tradeServiceImpl")
public class TradeServiceImpl implements TradeService {

    @Autowired
    private KeyGeneratorServiceClient keyGeneratorServiceClient;

    @Autowired
    private StockServiceClient stockServiceClient;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Autowired
    private TradeMapper tradeMapper;

    @Autowired
    private TradeItemMapper tradeItemMapper;

    @Autowired
    private MessageEventPublishMapper messageEventPublishMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public List<TradeItem> createOrder(List<TradeItem> tradeItemList) {
        // 唯一ID 雪花算法
        String orderNo = keyGeneratorServiceClient.keyGenerator();
        Long tradeNo = Long.parseLong(orderNo);
        Long userUUId = tradeItemList.get(0).getUserUuid();

        List<StockReduce> stockReduceList = new ArrayList<>();
        for (TradeItem item : tradeItemList) {
            StockReduce stockReduce = new StockReduce();
            stockReduce.setOrderNo(item.getTradeNo());
            stockReduce.setSkuId(item.getSkuId());
            stockReduce.setReduceCount(item.getQuantity());
            stockReduceList.add(stockReduce);
        }

        // 扣减库存
        ApiResult<Map<Long, Integer>> result = stockServiceClient.reduceStock(stockReduceList);
        Map<Long, Integer> stockResultMap = result.getData();

        // 查询相关sku属性
        List<ProductSku> skuResult = productSkuMapper.selectBySkuIdList(stockResultMap.keySet());

        // 生成订单
        Trade trade = new Trade();
        trade.setTradeNo(tradeNo);
        trade.setStatus(TradeStatus.WAITING_PAY.getValue());
        trade.setUserUuid(userUUId);
        tradeMapper.insertSelective(trade);
        // 插入tradeItem
        for (Long skuId : stockResultMap.keySet()) {
            if (stockResultMap.get(skuId) == -1) {
                // 扣减失败的商品
                TradeItem tradeItem = tradeItemList.stream().filter(
                        item -> item.getSkuId().equals(skuId)
                ).findFirst().get();
                tradeItemList.remove(tradeItem);
            }
        }

        tradeItemList.forEach(
                param -> {
                    ProductSku productSku = skuResult.stream().filter(
                            skuParam -> param.getSkuId().equals(skuParam.getId())
                    ).findFirst().get();
                    param.setTradeNo(tradeNo);
                    param.setSkuImageUrl(productSku.getImgUrl());
                    param.setSkuName(productSku.getSkuName());
                    param.setCurrentPrice(productSku.getSkuPrice());
                    param.setTotalPrice(productSku.getSkuPrice().multiply(new BigDecimal(param.getQuantity())));
                    tradeItemMapper.insertSelective(param);
                }
        );

        // 设置redis 订单号过期
        redisTemplate.opsForValue().set(tradeNo.toString(), tradeNo.toString(), 20, TimeUnit.MINUTES);
        return null;
    }


    /**
     * 订单支付，为了保证同时向用户账号添加积分，使用分布式事务，
     * 向数据库中的消息事件发布表中插入消息
     * @param out_trade_no 交易编号
     */
    @Override
    @Transactional
    public void payOrder(String out_trade_no) {
        Trade trade = tradeMapper.selectByTradeNo(out_trade_no);
        if (trade.getStatus() == TradeStatus.WAITING_PAY.getValue()) {
            trade.setStatus(TradeStatus.TRADE_PAIED.getValue());
            trade.setPaymentTime(new Date());
            trade.setPaymenyType((byte) 1);
            tradeMapper.updateByPrimaryKeySelective(trade);
        }
        // 插入消息表
        MessageEventPublish messageEventPublish = new MessageEventPublish();
        // 请求ID服务拿一个ID或者UUID作为消息的唯一标识
        String id = keyGeneratorServiceClient.keyGenerator();
        messageEventPublish.setId(Long.parseLong(id));
        String json = "用户积分json";
        messageEventPublish.setPayload(json);
        messageEventPublish.setStatus((byte) 1);
        messageEventPublish.setType((byte) 1);
        messageEventPublish.setCreateTime(new Date());
        messageEventPublishMapper.insertSelective(messageEventPublish);
        redisTemplate.delete(trade.getTradeNo().toString());
    }
}
