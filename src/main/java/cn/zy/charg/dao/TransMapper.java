package cn.zy.charg.dao;

import cn.zy.charg.bean.Trans;
import cn.zy.charg.bean.TransExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TransMapper {
    long countByExample(TransExample example);

    int deleteByExample(TransExample example);

    int insert(Trans record);

    int insertSelective(Trans record);

    List<Trans> selectByExample(TransExample example);

    int updateByExampleSelective(@Param("record") Trans record, @Param("example") TransExample example);

    int updateByExample(@Param("record") Trans record, @Param("example") TransExample example);
}