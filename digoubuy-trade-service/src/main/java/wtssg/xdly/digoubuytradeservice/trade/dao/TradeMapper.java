package wtssg.xdly.digoubuytradeservice.trade.dao;

import org.apache.ibatis.annotations.Mapper;
import wtssg.xdly.digoubuytradeservice.trade.entity.Trade;

@Mapper
public interface TradeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Trade record);

    int insertSelective(Trade record);

    Trade selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Trade record);

    int updateByPrimaryKey(Trade record);

    Trade selectByTradeNo(String out_trade_no);
}