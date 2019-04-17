package wtssg.xdly.digoubuytradeservice.trade.dao;

import org.apache.ibatis.annotations.Mapper;
import wtssg.xdly.digoubuytradeservice.trade.entity.ShoppingCart;

@Mapper
public interface ShoppingCartMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ShoppingCart record);

    int insertSelective(ShoppingCart record);

    ShoppingCart selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ShoppingCart record);

    int updateByPrimaryKey(ShoppingCart record);
}