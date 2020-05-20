package cn.zy.charg.service;

import cn.zy.charg.bean.BusinessErrorCode;
import cn.zy.charg.bean.BusinessException;
import cn.zy.charg.bean.PositionCity;
import cn.zy.charg.bean.PositionCityExample;
import cn.zy.charg.dao.PositionCityMapper;
import cn.zy.charg.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PositionCityService {
    @Autowired
    private PositionCityMapper positionCityMapper;
    public List<Map<String, Object>> getProvince(){
        return positionCityMapper.selectProvince();
    }

    public List<Map<String, Object>> getCity(Long province_id){
        return positionCityMapper.selectCity(province_id);
    }

    public List<Map<String, Object>> getCounty(Long city_id){
        return positionCityMapper.selectCounty(city_id);
    }

    public List<Map<String, Object>> getTown(Long county_id){
        return positionCityMapper.selectTown(county_id);
    }

    public String getCityIdByCityName(String city_name){
        String city_id = positionCityMapper.getCityIdByCityName(city_name);
        if(StrUtil.isEmptyStr(city_id)){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT);
        }
        return city_id;
    }
    public String getFullNameforTownId(long town_id){
        PositionCityExample example = new PositionCityExample();
        PositionCityExample.Criteria criteria = example.createCriteria();
        criteria.andTownIdEqualTo(town_id);
        List<PositionCity> list = positionCityMapper.selectByExample(example);
        String result = "";
        if(list!=null&&list.size()>0){
            PositionCity positionCity = list.get(0);
            result = positionCity.getProvinceName()+"-"+positionCity.getCityName()+"-"+positionCity.getCountyName()+"-"+positionCity.getTownName();
        }
        return result;

    }




}
