package cn.zy.charg.dao;

import cn.zy.charg.bean.Message;
import cn.zy.charg.bean.MessageExample;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface MessageMapper {
    long countByExample(MessageExample example);

    int deleteByExample(MessageExample example);

    int insert(Message record);

    int insertSelective(Message record);

    List<Message> selectByExample(MessageExample example);

    int updateByExampleSelective(@Param("record") Message record, @Param("example") MessageExample example);

    int updateByExample(@Param("record") Message record, @Param("example") MessageExample example);

    List<Map<String, Object>> selectUnReadUser();
}