package wtssg.xdly.digoubuytradeservice.product.dao;

import wtssg.xdly.digoubuytradeservice.product.entity.Property;

public interface PropertyMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Property record);

    int insertSelective(Property record);

    Property selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Property record);

    int updateByPrimaryKey(Property record);
}