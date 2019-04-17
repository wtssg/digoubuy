package wtssg.xdly.digoubuytradeservice.product.dao;

import org.apache.ibatis.annotations.Mapper;
import wtssg.xdly.digoubuytradeservice.product.entity.Category;
import wtssg.xdly.digoubuytradeservice.product.entity.Product;

import java.util.List;

@Mapper
public interface CategoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    List<Category> selectAll();

}