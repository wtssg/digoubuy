package wtssg.xdly.digoubuyschedulerservice.stock.dao;

import org.apache.ibatis.annotations.Mapper;
import wtssg.xdly.digoubuyschedulerservice.stock.entity.Stock;

@Mapper
public interface StockMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Stock record);

    int insertSelective(Stock record);

    Stock selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Stock record);

    int updateByPrimaryKey(Stock record);
}