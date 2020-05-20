package cn.zy.charg.dao;

import cn.zy.charg.bean.Maintainer;
import cn.zy.charg.bean.MaintainerExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MaintainerMapper {
    long countByExample(MaintainerExample example);

    int deleteByExample(MaintainerExample example);

    int insert(Maintainer record);

    int insertSelective(Maintainer record);

    List<Maintainer> selectByExample(MaintainerExample example);

    int updateByExampleSelective(@Param("record") Maintainer record, @Param("example") MaintainerExample example);

    int updateByExample(@Param("record") Maintainer record, @Param("example") MaintainerExample example);
}