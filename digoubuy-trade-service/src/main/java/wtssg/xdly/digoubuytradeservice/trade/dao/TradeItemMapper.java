package wtssg.xdly.digoubuytradeservice.trade.dao;

import org.apache.ibatis.annotations.Mapper;
import wtssg.xdly.digoubuytradeservice.trade.entity.TradeItem;

@Mapper
public interface TradeItemMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TradeItem record);

    int insertSelective(TradeItem record);

    TradeItem selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TradeItem record);

    int updateByPrimaryKey(TradeItem record);
}