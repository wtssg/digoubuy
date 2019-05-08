package wtssg.xdly.digoubuytradeservice.trade.dao;

import org.apache.ibatis.annotations.Mapper;
import wtssg.xdly.digoubuytradeservice.trade.entity.Trade;

import java.util.List;

@Mapper
public interface TradeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Trade record);

    int insertSelective(Trade record);

    Trade selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Trade record);

    int updateByPrimaryKey(Trade record);

    Trade selectByTradeNo(Long tradeNo);

    Trade selectByTradeNoAndUuid(Long tradeNo, Long uuid);

    List<Trade> selectedByUuid(Long uuid);
}