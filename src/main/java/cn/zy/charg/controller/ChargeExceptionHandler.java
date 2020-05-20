package cn.zy.charg.controller;

import cn.zy.charg.bean.BusinessException;
import cn.zy.charg.bean.Msg;


import cn.zy.charg.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ChargeExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ChargeExceptionHandler.class);
    //捕获全局异常,处理所有不可知的异常
    @ExceptionHandler(value=Exception.class)
    Object handleException(Exception e, HttpServletRequest request){
        LOG.error("RequestURL ：{}, Parameters:{}",request.getRequestURL(), StrUtil.getMapToString2(request.getParameterMap(),"***"));
        LOG.error("ErrorLocation: {}",e.getStackTrace()[0]);
        LOG.error("ErrorInfo:****Code: 200,Message: {} ",e.getMessage());
        e.printStackTrace();
        return Msg.fail().setNewMsg(e.getMessage());
        }

    @ExceptionHandler(value= BusinessException.class)
    Object handleException(BusinessException e, HttpServletRequest request){
        LOG.error("RequestURL ：{}, Parameters:{}",request.getRequestURL(), StrUtil.getMapToString2(request.getParameterMap(),"***"));
        LOG.error("ErrorLocation: {}",e.getStackTrace()[0]);
        LOG.error("ErrorInfo:****Code: {},Message: {} ",e.getCode(),e.getMessage());
        e.printStackTrace();
        Msg msg = Msg.fail();
        msg.setCode(e.getCode());
        msg.setMsg(e.getMessage());
        return msg;
    }
}
