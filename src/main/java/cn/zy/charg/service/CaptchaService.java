package cn.zy.charg.service;

import cn.zy.charg.bean.BusinessErrorCode;
import cn.zy.charg.bean.BusinessException;
import cn.zy.charg.bean.Captcha;
import cn.zy.charg.bean.CaptchaExample;
import cn.zy.charg.dao.CaptchaMapper;
import cn.zy.charg.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CaptchaService {
    @Autowired
    private CaptchaMapper captchaMapper;

    public String getCaptchaByPhoneIn5Min(String number){
        CaptchaExample example = new CaptchaExample();
        CaptchaExample.Criteria criteria = example.createCriteria();
        criteria.andContactEqualTo(number);
        new Date();
        criteria.andCreateTimeBetween(new Date(new Date().getTime()-5*60000),new Date());
        example.setOrderByClause("create_time desc");
        List<Captcha> list = captchaMapper.selectByExample(example);
        if(list.size()<=0){
            return null;
        }else return list.get(0).getCaptcha();
    }

//    public String getCaptchaByPhoneIn5Min(String number){
//        Captcha captcha = captchaMapper.selectCaptchaByTimeBetween(number, DateUtil.getDateTimeBefore(300),DateUtil.getDateTime());
//        if(captcha!=null){
//            return captcha.getCaptcha();
//        }
//        else return null;
//    }
    public void addCaptcha(String number,String captcha_code){
        Captcha captcha = new Captcha();
        captcha.setCaptcha(captcha_code);
        captcha.setContact(number);
        captcha.setCreateTime(new Date());
        captcha.setId(null);
        if(captchaMapper.insert(captcha)!=1){
            throw new BusinessException(BusinessErrorCode.DB_INSERTFAIL);
        }

    }

    public static void main(String[] args) {
        System.out.println(new Date(new Date().getTime()-5*60000));
    }

}
