package wtssg.xdly.digoubuytradeservice.product.dao;

import org.apache.ibatis.annotations.Mapper;
import wtssg.xdly.digoubuytradeservice.product.entity.Product;


@Mapper
public interface ProductMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    Product selectProductDetail(int productId);

}