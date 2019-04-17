package wtssg.xdly.digoubuytradeservice.trade.dao;

import org.apache.ibatis.annotations.Mapper;
import wtssg.xdly.digoubuytradeservice.trade.entity.MessageEventPublish;

@Mapper
public interface MessageEventPublishMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MessageEventPublish record);

    int insertSelective(MessageEventPublish record);

    MessageEventPublish selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MessageEventPublish record);

    int updateByPrimaryKey(MessageEventPublish record);
}