package cn.zy.charg.dao;

import cn.zy.charg.bean.PositionCity;
import cn.zy.charg.bean.PositionCityExample;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface PositionCityMapper {
    long countByExample(PositionCityExample example);

    int deleteByExample(PositionCityExample example);

    int insert(PositionCity record);

    int insertSelective(PositionCity record);

    List<PositionCity> selectByExample(PositionCityExample example);

    int updateByExampleSelective(@Param("record") PositionCity record, @Param("example") PositionCityExample example);

    int updateByExample(@Param("record") PositionCity record, @Param("example") PositionCityExample example);

    List<Map<String, Object>> selectProvince();

    List<Map<String, Object>> selectCity(@Param("province") Long province);

    List<Map<String, Object>> selectCounty(@Param("city") Long city);

    List<Map<String, Object>> selectTown(@Param("county") Long county);

    String getCityIdByCityName(@Param("city_name") String city_name);

}