package cn.zy.charg.dao;

import cn.zy.charg.bean.Captcha;
import cn.zy.charg.bean.CaptchaExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CaptchaMapper {
    long countByExample(CaptchaExample example);

    int deleteByExample(CaptchaExample example);

    int insert(Captcha record);

    int insertSelective(Captcha record);

    List<Captcha> selectByExample(CaptchaExample example);

    int updateByExampleSelective(@Param("record") Captcha record, @Param("example") CaptchaExample example);

    int updateByExample(@Param("record") Captcha record, @Param("example") CaptchaExample example);

    Captcha selectCaptchaByTimeBetween(@Param("phone") String phone,@Param("value1") String value1,@Param("value2") String value2);
}