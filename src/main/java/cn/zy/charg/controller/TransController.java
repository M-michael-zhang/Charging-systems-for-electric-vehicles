package cn.zy.charg.controller;

import cn.zy.charg.bean.*;
import cn.zy.charg.service.PileService;
import cn.zy.charg.service.TransService;
import cn.zy.charg.util.DateUtil;
import cn.zy.charg.util.DoubleUtil;
import cn.zy.charg.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RequestMapping("/trans")
@RestController
@Transactional
@CrossOrigin
public class TransController {
    @Autowired
    private TransService transService;

    @Autowired
    private PileService pileService;

    @RequestMapping("/getTrans")
    public Msg getTrans(@RequestParam(value = "pn",defaultValue = "1")Integer pn, Model model,
                        @RequestParam(value = "pz",defaultValue = "10")Integer pz){
        PageHelper.startPage(pn,pz);
        List<Trans> list = transService.getAll();
        if(list.size()<=0){
            throw new BusinessException(BusinessErrorCode.DB_EMPTYRESULT);
        }
        PageInfo page = new PageInfo(list,5);
        model.addAttribute("PageInfo",page);
        return Msg.success().add("Trans",page);
    }

    @RequestMapping("/getTransByUidWithoutPage")
    public Msg getTransByUidWithoutPage(int uid){
        List<Trans> list = transService.getTransByUid(uid);
        if(list==null||list.size()<=0){
            throw new BusinessException(BusinessErrorCode.DB_EMPTYRESULT);
        }
        for(Trans o:list){
            Pile p = pileService.getPile(o.getPid());
            o.setReserve2(p.getReserve1()+" "+p.getId()+"号");
        }
        return Msg.success().add("total",list.size()).add("Trans",list);
    }
    @RequestMapping("/getTransSelective")
    public Msg getTransSelective(@RequestParam(value = "pn",defaultValue = "1")Integer pn, Model model,
                                 @RequestParam(value = "pz",defaultValue = "10")Integer pz,HttpServletRequest request){
        Map<String,String[]> param;
        param = new HashMap(request.getParameterMap());
        for (Iterator<Map.Entry<String, String[]>> it = param.entrySet().iterator(); it.hasNext();){
            Map.Entry<String, String[]> item = it.next();
            if(StrUtil.isEmptyStr(StrUtil.getArrayToString(item.getValue()))){
                it.remove();
            }
        }
        List<Trans> list = transService.getTransSelective(param);
        PageHelper.startPage(pn,pz);
        if(list==null||list.size()<=0){
            throw new BusinessException(BusinessErrorCode.DB_EMPTYRESULT);
        }
        PageInfo page = new PageInfo(list,5);
        model.addAttribute("PageInfo",page);
        return Msg.success().add("Trans",page);
    }
    @RequestMapping("/updateTrans")
    public Msg updateTrans(HttpServletRequest request){
        Trans trans = new Trans();
        if(!StrUtil.isEmptyStr(request.getParameter("uid"))){
            trans.setUid(Integer.parseInt(request.getParameter("uid")));
        }
        if(!StrUtil.isEmptyStr(request.getParameter("pid"))){
            trans.setPid(Integer.parseInt(request.getParameter("pid")));
        }
        if(!StrUtil.isEmptyStr(request.getParameter("status"))){
            trans.setStatus(request.getParameter("status"));
        }
        if(!StrUtil.isEmptyStr(request.getParameter("type"))){
            trans.setType(request.getParameter("type"));
        }
        if(!StrUtil.isEmptyStr(request.getParameter("serialNo"))){
            trans.setSerialNo(request.getParameter("serialNo"));
        }else{
            throw new BusinessException(BusinessErrorCode.DB_UPDATEFAIL.setNewMessage("请上送交易流水号"));
        }
        if(!StrUtil.isEmptyStr(request.getParameter("amount"))){
            trans.setAmount(Double.parseDouble(request.getParameter("amount")));
        }
        if(!StrUtil.isEmptyStr(request.getParameter("transTime"))){
            trans.setTransTime(DateUtil.getFormatDate(request.getParameter("transTime")));
        }
        if(!StrUtil.isEmptyStr(request.getParameter("endTime"))){
            trans.setEndTime(DateUtil.getFormatDate(request.getParameter("endTime")));
        }
        if(trans.getEndTime()!=null&&trans.getTransTime()!=null){
            Long l = (trans.getEndTime().getTime()-trans.getTransTime().getTime());
            trans.setCostTime(DoubleUtil.getDecimalTo2(l.doubleValue()/3600000));
        }
        transService.updateTrans(trans);
        return Msg.success();
    }

    @RequestMapping("/deleteTrans")
    public Msg deleteTrans(HttpServletRequest request){
        String serial = request.getParameter("serial");
        if(StrUtil.isEmptyStr(serial)){
            throw new BusinessException(BusinessErrorCode.DB_UPDATEFAIL.setNewMessage("请上送交易流水号"));
        }
        transService.deleteTrans(serial);
        return Msg.success();
    }

}
