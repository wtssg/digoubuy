package wtssg.xdly.digoubuystockservice.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import wtssg.xdly.digoubuystockservice.stock.entity.Stock;

import java.util.Arrays;
import java.util.Collections;

@Component
public class RedisUtils {

    private static final Long EXECUTE_SUCCESS = 1L;
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";


    private static final String STOCK_CACHE_LUA =
            "local stock = KEYS[1] " +
            "local stock_lock = KEYS[2] " +
            "local stock_val = tonumber(ARGV[1]) " +
            "local stock_lock_val = tonumber(ARGV[2]) " +
            "local is_exists = redis.call(\"EXISTS\", stock) " +
            "if is_exists == 1  then " +
            "   return 0 " +
            "else  " +
            "   redis.call(\"SET\", stock, stock_val) " +
            "   redis.call(\"SET\", stock_lock, stock_lock_val) " +
            "   return 1 " +
            "end";;
    private static final String STOCK_REDUCE_LUA =
            "local stock = KEYS[1]\n" +
            "local stock_lock = KEYS[2]\n" +
            "local stock_change = tonumber(ARGV[1])\n" +
            "local stock_lock_change = tonumber(ARGV[2])\n" +
            "local is_exists = redis.call(\"EXISTS\", stock)\n" +
            "if is_exists == 1 then\n" +
            "    local stockAftChange = redis.call(\"DECRBY\", stock,stock_change)\n" +
            "    if(stockAftChange<0) then\n" +
            "        redis.call(\"INCRBY\", stock,stock_change)\n" +
            "        return -1\n" +
            "    else \n" +
            "        local stockLockAftChange = redis.call(\"INCRBY\", stock_lock,stock_lock_change)\n" +
            "        return {stockAftChange,stockLockAftChange}\n" +
            "    end " +
            "else \n" +
            "    return 0\n" +
            "end";
    private static final String UNLOCK_LUA =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "   return redis.call('del', KEYS[1]) " +
            "else " +
            "   return 0 " +
            "end";;


    @Autowired
    RedisTemplate redisTemplate;


    /**
     * 原子操作存库存
     * @param stockKey 库存key
     * @param lockStockKey 锁定库存key
     * @param stockValue 库存值
     * @param lockStockValue 锁定库存值
     * @return 1 : 操作成功
     */
    public boolean skuStockInit(String stockKey, String lockStockKey, String stockValue, String lockStockValue) {

        Object result = redisTemplate.execute((RedisCallback<Object>) redisConnection ->{
            Jedis jedis = (Jedis) redisConnection.getNativeConnection();
            return jedis.eval(STOCK_CACHE_LUA, Arrays.asList(stockKey, lockStockKey),
                    Arrays.asList(stockValue, lockStockValue));
        });

        return EXECUTE_SUCCESS.equals(result);
    }

    /**
     * 原子操作扣库存
     * @param stockKey 库存key
     * @param lockStockKey 锁定库存key
     * @param stockChange 库存改变值
     * @param lockStockChange 锁定库存改变值
     * @return List{改变后的库存，改变后的锁定库存}，-1 : 库存不足， 0 ：库存数据不存在
     */
    public Object reduceStock(String stockKey, String lockStockKey, String stockChange, String lockStockChange) {
        return redisTemplate.execute((RedisCallback<Object>) redisConnection ->{
            Jedis jedis = (Jedis) redisConnection.getNativeConnection();
            return jedis.eval(STOCK_REDUCE_LUA, Arrays.asList(stockKey, lockStockKey),
                    Arrays.asList(stockChange, lockStockChange));
        });
    }

    /**
     * 分布式锁
     * @param lockKey 锁的key
     * @param requestId 锁的value，用于解锁时的身份验证
     * @param expireTime 过期时间
     * @return true : 锁成功；false ：锁失败
     */
    public boolean distributeLock(String lockKey, String requestId, int expireTime) {
        Object result = redisTemplate.execute((RedisCallback<Object>) redisConnection -> {
            JedisCommands commands = (JedisCommands) redisConnection.getNativeConnection();
            return commands.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
        });
        return LOCK_SUCCESS.equals(result);
    }

    /**
     * 释放分布式锁
     * @param lockKey 锁的key
     * @param requestId 锁的值
     * @return true：释放锁成功，false:释放锁失败
     */
    public boolean releaseDistributeLock(String lockKey, String requestId) {
        Object result = redisTemplate.execute((RedisCallback<Object>) redisConnection ->{
            Jedis jedis = (Jedis) redisConnection.getNativeConnection();
            return jedis.eval(UNLOCK_LUA, Collections.singletonList(lockKey),
                    Collections.singletonList(requestId));
        });

        return EXECUTE_SUCCESS.equals(result);
    }
}
