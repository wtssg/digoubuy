package wtssg.xdly.digoubuytradeservice.trade.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wtssg.xdly.digoubuytradeservice.trade.entity.ShoppingCart;
import wtssg.xdly.digoubuytradeservice.trade.entity.ShoppingCartItem;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ShoppingCart record);

    int insertSelective(ShoppingCart record);

    ShoppingCart selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ShoppingCart record);

    int updateByPrimaryKey(ShoppingCart record);

    List<ShoppingCartItem> selectByUuid(long uuid);

    void deleteByPrimaryKeyList(@Param("idList")List<Long> idList);

    ShoppingCart selectByShoppingCart(ShoppingCart shoppingCart);

    List<ShoppingCartItem> selectByUuidAndStatus(Long uuid);
}