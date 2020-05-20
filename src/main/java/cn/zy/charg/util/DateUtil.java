package cn.zy.charg.util;

import cn.zy.charg.bean.BusinessErrorCode;
import cn.zy.charg.bean.BusinessException;
import cn.zy.charg.dao.CaptchaMapper;
import cn.zy.charg.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {
    public static void main(String[] args){
        String a ="2135421123123";
        System.out.println(a.substring(0,4));
    }
//    dateStr+(int)(Math.random()*9+1)
    public static String getDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        Date date = new Date();
        String dateStr = sdf.format(date);
        return dateStr;
    }

    public static Date getFormatDate(String dateString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone((TimeZone.getTimeZone("Asia/Shanghai")));
        Date date;
        try {
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new BusinessException(BusinessErrorCode.FORMAT_FAULT);
        }
        return date;
    }

    public static String getDateTimeBefore(int seconds){
        Date date = new Date(new Date().getTime()-seconds*1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String dateStr = sdf.format(date);
        return dateStr;
    }
    public static String getDateTimeBefore(int seconds,Date date){
        Date beforeDate = new Date(date.getTime()-seconds*1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String dateStr = sdf.format(beforeDate);
        return dateStr;
    }
    //获取日期当天0点 格式20200202
    public static Date  getBeginTimeFromDate(String date){
        date+="000000000";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        //当天0点
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
           throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("格式转换错误"));
        }
    }
    //获取日期后一天0点 格式20200202
    public static Date  getEndTimeFromDate(String date){
        date+="000000000";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return new Date(getBeginTimeFromDate(date).getTime()+24*60*60*1000);
    }


}
