package cn.zy.charg.dao;

import cn.zy.charg.bean.Pile;
import cn.zy.charg.bean.PileExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface PileMapper {
    long countByExample(PileExample example);

    int deleteByExample(PileExample example);

    int insert(Pile record);

    int insertSelective(Pile record);

    List<Pile> selectByExample(PileExample example);

    int updateByExampleSelective(@Param("record") Pile record, @Param("example") PileExample example);

    int updateByExample(@Param("record") Pile record, @Param("example") PileExample example);

    Pile selectByPrimaryKey(Integer id);

    void updateByPrimaryKeySelective(Pile pile);

    void deleteByPrimaryKey(Integer id);
}