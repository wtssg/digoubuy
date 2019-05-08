package wtssg.xdly.digoubuytradeservice.trade.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wtssg.xdly.digoubuytradeservice.common.constants.Constants;
import wtssg.xdly.digoubuytradeservice.common.enums.TradeStatus;
import wtssg.xdly.digoubuytradeservice.common.exception.DigoubuyException;
import wtssg.xdly.digoubuytradeservice.common.resp.ApiResult;
import wtssg.xdly.digoubuytradeservice.product.dao.ProductSkuMapper;
import wtssg.xdly.digoubuytradeservice.product.entity.ProductSku;
import wtssg.xdly.digoubuytradeservice.trade.dao.MessageEventPublishMapper;
import wtssg.xdly.digoubuytradeservice.trade.dao.ShoppingCartMapper;
import wtssg.xdly.digoubuytradeservice.trade.dao.TradeItemMapper;
import wtssg.xdly.digoubuytradeservice.trade.dao.TradeMapper;
import wtssg.xdly.digoubuytradeservice.trade.entity.*;
import wtssg.xdly.digoubuytradeservice.trade.feign.KeyGeneratorServiceClient;
import wtssg.xdly.digoubuytradeservice.trade.feign.StockServiceClient;

import java.math.BigDecimal;
import java.util.*;
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
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private MessageEventPublishMapper messageEventPublishMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Override
    @Transactional
    public void putIntoCart(ShoppingCartItem cartItem, long uuid) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserUuid(uuid);
        shoppingCart.setSkuId(cartItem.getSkuId());
        shoppingCart.setSkuNum(cartItem.getSkuNum());
        ShoppingCart curCartItem = shoppingCartMapper.selectByShoppingCart(shoppingCart);
        if (curCartItem != null) {
            // 商品已经在购物车中，更新商品数量
            curCartItem.setSkuNum(curCartItem.getSkuNum() + cartItem.getSkuNum());
            shoppingCartMapper.updateByPrimaryKey(curCartItem);
        } else {
            // 商品不在购物车中，插入记录
            shoppingCartMapper.insertSelective(shoppingCart);
        }
    }

    @Override
    public void changeCartItemNum(ShoppingCart shoppingCart) {
        shoppingCartMapper.updateByPrimaryKeySelective(shoppingCart);
    }

    @Override
    public void delCartItems(List<Long> idList) {
        shoppingCartMapper.deleteByPrimaryKeyList(idList);
    }

    @Override
    @Transactional
    public List<ShoppingCartItem> getCartItem(long uuid, boolean selected) {
        List<ShoppingCartItem> shoppingCartItemList;
        if (selected) {
            shoppingCartItemList = shoppingCartMapper.selectByUuidAndStatus(uuid);
        } else {
            shoppingCartItemList = shoppingCartMapper.selectByUuid(uuid);
        }

        TreeMap<Long, ShoppingCartItem> shoppingCartItemMap = new TreeMap<>();
        for (int i = 0; i < shoppingCartItemList.size(); i++) {
            ShoppingCartItem shoppingCartItem = shoppingCartItemList.get(i);
            shoppingCartItemMap.put(shoppingCartItem.getSkuId(), shoppingCartItem);
        }
        List<ProductSku> productSkuList = productSkuMapper.selectBySkuIdList(shoppingCartItemMap.keySet());
        List<ShoppingCartItem> resultList = new ArrayList<>();
        for (int i = 0; i < productSkuList.size(); i++) {
            ProductSku productSku = productSkuList.get(i);
            ShoppingCartItem shoppingCartItem = shoppingCartItemMap.get(productSku.getId());
            shoppingCartItem.setSpuId(productSku.getSpuId());
            shoppingCartItem.setImgUrl(productSku.getImgUrl());
            shoppingCartItem.setSkuName(productSku.getSkuName());
            shoppingCartItem.setSkuPrice(productSku.getSkuPrice());
            resultList.add(shoppingCartItem);
        }
        return resultList;
    }

    /**
     * 购物车生成订单
     * @param uuid
     * @return
     */
    @Override
    @Transactional
    public Trade createOrderByCart(Long addressId, Long uuid) {
        List<TradeItem> tradeItemList = new ArrayList<>();
        List<ShoppingCartItem> shoppingCartList = shoppingCartMapper.selectByUuidAndStatus(uuid);
        List<Long> cartIdList = new ArrayList<>();
        // 唯一ID 雪花算法
        String orderNo = keyGeneratorServiceClient.keyGenerator();
        Long tradeNo = Long.parseLong(orderNo);

        List<StockReduce> stockReduceList = new ArrayList<>();
        for (ShoppingCartItem cartItem : shoppingCartList) {
            cartIdList.add(cartItem.getId());

            TradeItem tradeItem = new TradeItem();
            tradeItem.setSkuId(cartItem.getSkuId());
            tradeItem.setQuantity(cartItem.getSkuNum());
            tradeItem.setUserUuid(uuid);
            tradeItemList.add(tradeItem);

            StockReduce stockReduce = new StockReduce();
            stockReduce.setOrderNo(tradeNo);
            stockReduce.setSkuId(cartItem.getSkuId());
            stockReduce.setReduceCount(cartItem.getSkuNum());
            stockReduceList.add(stockReduce);
        }

        // 订单商品从购物车中删除
        shoppingCartMapper.deleteByPrimaryKeyList(cartIdList);

        // 扣减库存
        ApiResult<Map<Long, Integer>> result = stockServiceClient.reduceStock(stockReduceList);
        Map<Long, Integer> stockResultMap = result.getData();

        Map<Long, Integer> realStockResultMap = new HashMap<>();
        for (Long skuId : stockResultMap.keySet()) {
            if (stockResultMap.get(skuId) != -1) {
                // 扣减失败的商品
                realStockResultMap.put(skuId, stockResultMap.get(skuId));
            }
        }

        // 查询相关sku属性
        List<ProductSku> skuResult = productSkuMapper.selectBySkuIdList(realStockResultMap.keySet());

        // 生成订单
        Trade trade = new Trade();
        trade.setTradeNo(tradeNo);
        trade.setStatus(TradeStatus.WAITING_PAY.getValue());
        trade.setUserUuid(uuid);
        trade.setAddressId(addressId);
        BigDecimal totalPrice =new BigDecimal(0);

        for (ProductSku productSku : skuResult) {
            TradeItem tradeItem = new TradeItem();
            tradeItem.setTradeNo(tradeNo);
            tradeItem.setSkuImageUrl(productSku.getImgUrl());
            tradeItem.setSkuName(productSku.getSkuName());
            for (TradeItem aTradeItemList : tradeItemList) {
                if (productSku.getId().equals(aTradeItemList.getSkuId())) {
                    tradeItem.setQuantity(aTradeItemList.getQuantity());
                }
            }
            tradeItem.setCurrentPrice(productSku.getSkuPrice());
            tradeItem.setTotalPrice(productSku.getSkuPrice().multiply(new BigDecimal(tradeItem.getQuantity())));
            totalPrice = totalPrice.add(tradeItem.getTotalPrice());
            tradeItemMapper.insertSelective(tradeItem);
        }

        trade.setTotalPrice(totalPrice);
        tradeMapper.insertSelective(trade);

        // 设置redis 订单号过期
        redisTemplate.opsForValue().set(tradeNo.toString(), tradeNo.toString(), 60, TimeUnit.MINUTES);
        trade.setTradeNoString(tradeNo.toString());
        return trade;
    }

    /**
     * 单个商品生成订单
     */
    @Override
    @Transactional
    public TradeItem createOrderByOne(Long addressId, Long uuid) {

        TradeItem tradeItem = getTempOrder(uuid);
        if (tradeItem == null) {
            throw new DigoubuyException("订单过期");
        }
        tradeItem.setAddressId(addressId);

        // 唯一ID 雪花算法
        String orderNo = keyGeneratorServiceClient.keyGenerator();
        Long tradeNo = Long.parseLong(orderNo);

        List<StockReduce> stockReduceList = new ArrayList<>();
        StockReduce stockReduce = new StockReduce();
        stockReduce.setOrderNo(tradeNo);
        stockReduce.setSkuId(tradeItem.getSkuId());
        stockReduce.setReduceCount(tradeItem.getQuantity());
        stockReduceList.add(stockReduce);

        // 扣减库存
        ApiResult<Map<Long, Integer>> result = stockServiceClient.reduceStock(stockReduceList);
        Map<Long, Integer> stockResultMap = result.getData();

        if (stockResultMap.get(tradeItem.getSkuId()) == -1) {
            // 扣减库存失败
            throw new DigoubuyException("生成订单失败");
        }

        // 生成订单
        Trade trade = new Trade();
        trade.setTradeNo(tradeNo);
        trade.setStatus(TradeStatus.WAITING_PAY.getValue());
        trade.setUserUuid(tradeItem.getUserUuid());
        trade.setAddressId(tradeItem.getAddressId());

        BigDecimal totalPrice = new BigDecimal(0);
        ProductSku productSku = productSkuMapper.selectByPrimaryKey(tradeItem.getSkuId());
        tradeItem.setTradeNo(tradeNo);
        tradeItem.setSkuImageUrl(productSku.getImgUrl());
        tradeItem.setSkuName(productSku.getSkuName());
        tradeItem.setCurrentPrice(productSku.getSkuPrice());
        tradeItem.setTotalPrice(productSku.getSkuPrice().multiply(new BigDecimal(tradeItem.getQuantity())));
        totalPrice = totalPrice.add(tradeItem.getTotalPrice());
        tradeItemMapper.insertSelective(tradeItem);

        trade.setTotalPrice(totalPrice);
        tradeMapper.insertSelective(trade);

        redisTemplate.opsForValue().set(tradeNo.toString(), tradeNo.toString(), 60, TimeUnit.MINUTES);
        tradeItem.setTradeNoString(tradeItem.getTradeNo().toString());
        return tradeItem;
    }


    @Override
    public void createTempOrder(TradeItem item) {
        String value = item.getSkuId() + "&" +item.getQuantity();
        redisTemplate.opsForValue().set(Constants.CACHE_TEMP_ORDER + item.getUserUuid().toString(), value , 5, TimeUnit.MINUTES);
    }

    @Override
    @Transactional
    public TradeItem getTempOrder(Long uuid) {
        String value = redisTemplate.opsForValue().get(Constants.CACHE_TEMP_ORDER + uuid.toString());
        TradeItem item = new TradeItem();
        if (value == null) {
            throw new DigoubuyException("订单过期");
        }
        String[] sa = value.split("&");
        item.setUserUuid(uuid);
        item.setSkuId(Long.valueOf(sa[0]));
        item.setQuantity(Integer.valueOf(sa[1]));
        ProductSku productSku = productSkuMapper.selectByPrimaryKey(item.getSkuId());
        item.setSkuImageUrl(productSku.getImgUrl());
        item.setCurrentPrice(productSku.getSkuPrice());
        item.setSkuName(productSku.getSkuName());
        item.setSpuId(productSku.getSpuId());
        return item;
    }

    @Override
    public Trade getTrade(Long tradeNo, Long uuid) {
        String s = redisTemplate.opsForValue().get(tradeNo.toString());
        Long seconds = redisTemplate.getExpire(tradeNo.toString());
        Trade trade = tradeMapper.selectByTradeNoAndUuid(tradeNo, uuid);
        trade.setTradeNoString(tradeNo.toString());
        for (TradeItem item : trade.getTradeItemList()) {
            item.setTradeNoString(tradeNo.toString());
        }
        trade.setSeconds(seconds);
        return trade;
    }

    /**
     * 订单支付，为了保证同时向用户账号添加积分，使用分布式事务，
     * 向数据库中的消息事件发布表中插入消息
     * @param out_trade_no 交易编号
     */
    @Override
    @Transactional
    public void payOrder(Long out_trade_no, BigDecimal payment) {
        Trade trade = tradeMapper.selectByTradeNo(out_trade_no);
        if (trade.getStatus() == TradeStatus.WAITING_PAY.getValue()) {
            trade.setStatus(TradeStatus.TRADE_PAIED.getValue());
            trade.setPayment(payment);
            trade.setPaymentTime(new Date());
            trade.setPaymenyType((byte) 1);
            tradeMapper.updateByPrimaryKeySelective(trade);
        }
        // 插入消息表
        MessageEventPublish messageEventPublish = new MessageEventPublish();
        // 请求ID服务拿一个ID或者UUID作为消息的唯一标识
        String id = keyGeneratorServiceClient.keyGenerator();
        messageEventPublish.setId(Long.parseLong(id));
        String json = String.valueOf(payment.longValue() / 10);
        messageEventPublish.setPayload(json);
        messageEventPublish.setStatus((byte) 1);
        messageEventPublish.setType((byte) 1);
        messageEventPublish.setCreateTime(new Date());
        messageEventPublishMapper.insertSelective(messageEventPublish);
        redisTemplate.delete(trade.getTradeNo().toString());
    }

    @Override
    public List<Trade> getAllTrade(Long uuid) {
        List<Trade> tradeList =  tradeMapper.selectedByUuid(uuid);
        for (Trade trade : tradeList) {
            trade.setTradeNoString(String.valueOf(trade.getTradeNo()));
        }
        return tradeList;
    }
}
