package cn.zy.charg.controller;

import cn.zy.charg.bean.Msg;
import cn.zy.charg.service.PositionCityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/position")
@RestController
@Transactional
@CrossOrigin
public class PositionCityController {
    @Autowired
    private PositionCityService positionCityService;

    @RequestMapping("/getProvince")
    public Msg getProvince(){
        return Msg.success().add("Province",positionCityService.getProvince());
    }

    @RequestMapping("/getCity")
    public Msg getCity(HttpServletRequest request){
        Long province_id = Long.parseLong(request.getParameter("provinceId"));
        return Msg.success().add("City",positionCityService.getCity(province_id));
    }

    @RequestMapping("/getCounty")
    public Msg getCounty(HttpServletRequest request){
        Long city_id = Long.parseLong(request.getParameter("cityId"));
        return Msg.success().add("County",positionCityService.getCounty(city_id));
    }

    @RequestMapping("/getTown")
    public Msg getTown(HttpServletRequest request){
        Long county_id = Long.parseLong(request.getParameter("countyId"));
        return Msg.success().add("Town",positionCityService.getTown(county_id));
    }

}
