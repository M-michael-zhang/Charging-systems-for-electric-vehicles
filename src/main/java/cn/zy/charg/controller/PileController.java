package cn.zy.charg.controller;
import cn.zy.charg.service.PositionCityService;
import cn.zy.charg.service.TransService;
import cn.zy.charg.service.UserService;
import cn.zy.charg.util.ChargeException;
import cn.zy.charg.util.DateUtil;
import cn.zy.charg.util.DoubleUtil;
import cn.zy.charg.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.zy.charg.service.PileService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import cn.zy.charg.bean.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
@RequestMapping("/pile")
@RestController
@Transactional
@CrossOrigin
public class PileController {
    @Autowired
    private PileService pileService;

    @Autowired
    private TransService transService;

    @Autowired
    private UserService userService;

    @Autowired
    private PositionCityService positionCityService;

    @RequestMapping("/getPiles")
    public Msg getpiles(@RequestParam(value = "pn",defaultValue = "1")Integer pn, Model model,
                        @RequestParam(value = "pz",defaultValue = "10")Integer pz){
        PageHelper.startPage(pn,pz);
        List<Pile> piles = pileService.getAll();
        PageInfo page = new PageInfo(piles,5);
        model.addAttribute("PageInfo",page);
        return Msg.success().add("Piles",page);
        //return Msg.success().add("piles",piles);
    }

    @RequestMapping("/getPilesByPosition")
    public Msg getPilesByPosition(HttpServletRequest request){
        String city_id = request.getParameter("cityId");
        String city_name = request.getParameter("cityName");
        if(StrUtil.isEmptyStr(city_id)){
            city_id = positionCityService.getCityIdByCityName(city_name);
        }
        List<Pile> list = pileService.getPileByPosition(city_id);
        return Msg.success().add("Pile",list);

    }

    @RequestMapping("/getPileById")
    public Msg getpileById(@RequestParam(value = "pn",defaultValue = "1")Integer pn, Model model,Integer id,
                           @RequestParam(value = "pz",defaultValue = "10")Integer pz){
//        if(StrUtil.isEmptyStr(request.getParameter("id"))){
//            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入充电桩编号"));
//        }
//        Integer id = Integer.valueOf(request.getParameter("id"));
        System.out.println("************");
        PageHelper.startPage(pn,pz);
        Pile pile = pileService.getPile(id);
        if(pile==null){
            return Msg.fail().setNewMsg("不存在该编号充电桩");
        }
        List<Pile> list = new ArrayList<>();
        list.add(pile);
        PageInfo page = new PageInfo(list,5);
        model.addAttribute("PageInfo",page);
        return Msg.success().add("Piles",page);
    }

    @RequestMapping("/addPile")
    public Msg addpile(HttpServletRequest request) throws ChargeException {
        if(StrUtil.isEmptyStr(request.getParameter("locationX"))||StrUtil.isEmptyStr(request.getParameter("locationY"))){
            return Msg.fail().setNewMsg("请设置充电桩经纬度");
        }
        Pile pile = buildPileFromRequest(request);
        pile.setId(null);
        if(pile.getCreateTime()!=null){
            pile.setCreateTime(new Date());
        }
        if(!StrUtil.isEmptyStr(pile.getAddress())){
            pile.setReserve1(positionCityService.getFullNameforTownId(Long.parseLong(pile.getAddress())));
        }
        pileService.addPile(pile);
        return Msg.success().setNewMsg("插入成功");
    }

    @RequestMapping("/deletePile")
    public Msg deletePile(HttpServletRequest request){
        if(StrUtil.isEmptyStr(request.getParameter("id"))){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入充电桩编号"));
        }
        Integer id = Integer.parseInt(request.getParameter("id"));
        if(pileService.isExistPile(id)){
            pileService.deletePile(id);
            return Msg.success().setNewMsg("删除成功，已删除编号为："+id+"的充电桩");
        } else{
            return Msg.fail().setNewMsg("删除失败,该编号不存在");
        }

    }

    @RequestMapping("/updatePile")
    public Msg updatePile(HttpServletRequest request){
        if(StrUtil.isEmptyStr(request.getParameter("id"))||!pileService.isExistPile(Integer.parseInt(request.getParameter("id")))){
            throw new BusinessException(BusinessErrorCode.DB_EMPTYRESULT);
        }
        Pile pile = buildPileFromRequest(request);
        pileService.updatePile(pile);
        return Msg.success().add("pile",pile).setNewMsg("更新充电桩成功");
    }

    @RequestMapping("/getPileByQRCode")
    public Msg getPileByQRCode(HttpServletRequest request) {
        if(StrUtil.isEmptyStr(request.getParameter("QRCode"))){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入二维码代码"));
        }
        Pile pile = pileService.getPileByQRCode(request.getParameter("QRCode"));
        if(pile==null){
            return Msg.fail().setNewMsg("未查询到该充电桩");
        }
        return Msg.success().add("pile",pile);
    }

    @RequestMapping("/bookPile")
    public Msg bookPile(HttpServletRequest request){
        //新增预约trans
        if(StrUtil.isEmptyStr(request.getParameter("uid"))||StrUtil.isEmptyStr(request.getParameter("pid"))){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入用户id和充电桩id"));
        }
        int uid = Integer.parseInt(request.getParameter("uid"));
        int pid = Integer.parseInt(request.getParameter("pid"));
        checkTrans(pid,uid);
        Trans trans =new Trans(null,uid,pid,new Date(),"0","0",null,null,null,null,null);
        transService.addTrans(trans);
        pileService.updatePileStatus(pid,"2");
        return Msg.success().setNewMsg("预约成功，请30分钟内开启充电");
    }


    @RequestMapping("/usePile")
    public Msg usePile(HttpServletRequest request){
        //新增预约trans
        if(StrUtil.isEmptyStr(request.getParameter("uid"))||StrUtil.isEmptyStr(request.getParameter("pid"))){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入用户id和充电桩id"));
        }
        int uid = Integer.parseInt(request.getParameter("uid"));
        int pid = Integer.parseInt(request.getParameter("pid"));
        //若已预约 则关闭预约的订单 释放充电桩
        Trans bookingTrans = transService.getTransInBooking(uid,pid);
        if(bookingTrans!=null){
            bookingTrans.setStatus("1");
            bookingTrans.setEndTime(new Date());
            Long l = (bookingTrans.getEndTime().getTime()-bookingTrans.getTransTime().getTime());
            String str =String.format("%.2f", l.doubleValue()/3600000);
            bookingTrans.setCostTime(Double.parseDouble(str));
            transService.updateTrans(bookingTrans);
            pileService.updatePileStatus(bookingTrans.getPid(),"0");
        }
        checkTrans(pid,uid);
        Trans trans =new Trans(null,uid,pid,new Date(),"1","0",null,null,null,null,null);
        transService.addTrans(trans);
        //pileService.updatePileStatus(pid,"1");
        //校验充电桩插头是否插上
        if(pileService.getPile(pid).getReserve2().split("~")[0].equals("0")){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("电源插头未接通"));
        }
        pileService.updateChargeStatue(pid,"1");
        //主动发送硬件 充电请求
        //判断余额是否够
        return Msg.success().setNewMsg("充电成功");
    }

    @RequestMapping("/endUsePile")
    public Msg endUsePile(HttpServletRequest request){
        String serial = request.getParameter("serial");
        int uid;
        int pid;
        Trans trans;
        if(!StrUtil.isEmptyStr(serial)){
            trans = transService.getTrans(serial);
            if(!trans.getStatus().equals("0")||!trans.getType().equals("1")){
                throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("该流水号未在充电进行中"));
            }
        }else if(!StrUtil.isEmptyStr(request.getParameter("uid"))&&!StrUtil.isEmptyStr(request.getParameter("pid"))){
            uid = Integer.parseInt(request.getParameter("uid"));
            pid = Integer.parseInt(request.getParameter("pid"));
            trans = transService.getTransInUsing(uid,pid);
        }else{
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入正确的预约信息"));
        }
        if(trans!=null) {
            trans.setStatus("1");
            trans.setEndTime(new Date());
            Long l = (trans.getEndTime().getTime()-trans.getTransTime().getTime());
            trans.setCostTime(DoubleUtil.getDecimalTo2(l.doubleValue()/3600000));
            double amount = pileService.getPile(trans.getPid()).getRate()*(l.doubleValue()/3600000);
            trans.setAmount(DoubleUtil.getDecimalTo2(amount));

            transService.updateTrans(trans);
            pileService.updatePileStatus(trans.getPid(),"0");
            //这里可能两种方式
            // 1.循环插件轮询查看充电量 主动发送硬件 结束充电
            // 2.硬件结束充电，主动发送请求给服务器
            //还需要调用支付功能 或者直接从账户扣款
            return Msg.success().setNewMsg("已关闭该交易");
        }
        return Msg.fail().setNewMsg("未查询到充电信息");
    }
    @RequestMapping("/endTrans")
    public Msg endTrans(HttpServletRequest request){
        String serial = request.getParameter("serial");
        Trans trans;
        if(!StrUtil.isEmptyStr(serial)){
            trans = transService.getTrans(serial);
            if(trans==null){
                throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("该流水号不存在"));
            }else if(!trans.getStatus().equals("0")){
                throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("该交易已结束"));
            }else{
                trans.setStatus("1");
                trans.setEndTime(new Date());
                Long l = (trans.getEndTime().getTime()-trans.getTransTime().getTime());
                trans.setCostTime(DoubleUtil.getDecimalTo2(l.doubleValue()/3600000));
                if(trans.getType().equals("1")){
                    double amount = pileService.getPile(trans.getPid()).getRate()*(l.doubleValue()/3600000);
                    trans.setAmount(DoubleUtil.getDecimalTo2(amount));
                }
                transService.updateTrans(trans);
                pileService.finishChargeFromClient(trans.getPid());
                //这里可能两种方式
                // 1.循环插件轮询查看充电量 主动发送硬件 结束充电
                // 2.硬件结束充电，主动发送请求给服务器
                //还需要调用支付功能 或者直接从账户扣款
            }
        }else{
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请上送流水号"));
        }
        return Msg.success().setNewMsg("已关闭该交易").add("Trans",trans);
    }
    @RequestMapping("/endBookPile")
    public Msg endBookPile(HttpServletRequest request){
        //修改原预约trans
        String serial = request.getParameter("serial");
        int uid;
        int pid;
        Trans trans;
        if(!StrUtil.isEmptyStr(serial)){
            trans = transService.getTrans(serial);
            if(!trans.getStatus().equals("0")||!trans.getType().equals("0")){
                throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("该流水号未在预约进行中"));
            }
        }else if(!StrUtil.isEmptyStr(request.getParameter("uid"))&&!StrUtil.isEmptyStr(request.getParameter("pid"))){
            uid = Integer.parseInt(request.getParameter("uid"));
            pid = Integer.parseInt(request.getParameter("pid"));
            trans = transService.getTransInBooking(uid,pid);
        }else{
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入正确的预约信息"));
        }
        if(trans!=null) {
            trans.setStatus("1");
            trans.setEndTime(new Date());
            Long l = (trans.getEndTime().getTime()-trans.getTransTime().getTime());
            String str =String.format("%.2f", l.doubleValue()/3600000);
            trans.setCostTime(Double.parseDouble(str));
            transService.updateTrans(trans);
            pileService.updatePileStatus(trans.getPid(),"0");
            return Msg.success().setNewMsg("已关闭该预约");
        }
        return Msg.fail().setNewMsg("未查询到预约信息");
    }

    @RequestMapping("/getPileFromQRCode")
    public Msg getPileFromQRCode(HttpServletRequest request){
        String qrcode = request.getParameter("QRCode");
        if(StrUtil.isEmptyStr(qrcode)){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请上送QRCode"));
        }
        Pile pile = pileService.getPileByQRCode(qrcode);
        if(pile==null){
            throw new BusinessException(BusinessErrorCode.DB_EMPTYRESULT);
        }
        return Msg.success().add("Pile",pile);
    }

    @RequestMapping("/updateReserve2")
    public Msg updateReserve2(HttpServletRequest request){
        int pid = Integer.parseInt(request.getParameter("id"));
        String plug = request.getParameter("plug");
        pileService.updatePlugStatue(pid,plug);
       return Msg.success();
    }

    public static void main(String[] args) throws ParseException {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
//        simpleDateFormat.setTimeZone((TimeZone.getTimeZone("Asia/Shanghai")));
//        Date date = simpleDateFormat.parse("2020-02-22 21:40:00");
//        System.out.println("2020-02-22 21:40:00");
//        System.out.println(date);
//        System.out.println(simpleDateFormat.format(date));
        String timeStr = "2020-02-22 21:40:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置北京时区
        Date d = sdf.parse(timeStr);
        System.out.println(d);
        System.out.println(sdf.format(d));
    }
    public Pile buildPileFromRequest(HttpServletRequest request){
        Pile pile = new Pile();
        try{
            if(!StrUtil.isEmptyStr(request.getParameter("id"))){
                pile.setId(Integer.parseInt(request.getParameter("id")));
            }
            if(!StrUtil.isEmptyStr(request.getParameter("cid"))){
                pile.setCid(Integer.parseInt(request.getParameter("cid")));
            }
            pile.setType(request.getParameter("type"));
            pile.setAddress(request.getParameter("address"));
            if(!StrUtil.isEmptyStr(request.getParameter("locationX"))){
                pile.setLocationX(Double.parseDouble(request.getParameter("locationX")));
            }
            if(!StrUtil.isEmptyStr(request.getParameter("locationY"))){
                pile.setLocationY(Double.parseDouble(request.getParameter("locationY")));
            }
            if(!StrUtil.isEmptyStr(request.getParameter("voltage"))){
                pile.setVoltage(Double.parseDouble(request.getParameter("voltage")));
            }
            if(!StrUtil.isEmptyStr(request.getParameter("electricity"))){
                pile.setElectricity(Double.parseDouble(request.getParameter("electricity")));
            }
            pile.setStatus(request.getParameter("status"));

            if(!StrUtil.isEmptyStr(request.getParameter("createTime"))){
                System.out.println(request.getParameter("createTime"));
                Date date = DateUtil.getFormatDate(request.getParameter("createTime"));
                pile.setCreateTime(date);
            }
            pile.setQrcode(request.getParameter("qrcode"));
            pile.setQrcodePath(request.getParameter("qrcodePath"));
            if(!StrUtil.isEmptyStr(request.getParameter("rate"))){
                pile.setRate(Double.parseDouble(request.getParameter("rate")));
            }
            pile.setReserve1(request.getParameter("reserve1"));
            pile.setReserve1(request.getParameter("reserve1"));
        } catch (NumberFormatException e){
            e.printStackTrace();
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("格式转换失败"));
        } catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT);
        }
        return pile;
    }

    public void checkTrans(int pid,int uid){
        //校验用户和充电桩是否存在
        if(pileService.getPile(pid)==null||!userService.isExistUser(uid)){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("用户或充电桩不存在"));
        }
        //查看该充电桩是否空闲
        if(!pileService.getPile(pid).getStatus().equals("0")){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("该充电桩已被使用，请选择其他充电桩"));
        }
        //查看用户预约数量，最多预约两个
        if(transService.getDoingTrans(uid)!=null&&transService.getDoingTrans(uid).size()>=2){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("抱歉，用户最多进行2个预约或实时充电订单"));
        }
    }

}


 /*           if(request.getParameter("cid")!=null){
                    pile.setCid(Integer.parseInt(request.getParameter("cid")));
                    }
                    if(request.getParameter("type")!=null){
                    pile.setType(request.getParameter("type"));
                    }
                    if(request.getParameter("address")!=null){
                    pile.setAddress(request.getParameter("address"));
                    }
                    if(request.getParameter("locationX")!=null){
                    pile.setLocationX(Double.parseDouble(request.getParameter("locationX")));
                    }
                    if(request.getParameter("locationY")!=null){
                    pile.setLocationY(Double.parseDouble(request.getParameter("locationY")));
                    }
                    if(request.getParameter("voltage")!=null){
                    pile.setVoltage(Double.parseDouble(request.getParameter("voltage")));
                    }
                    if(request.getParameter("electricity")!=null){
                    pile.setElectricity(Double.parseDouble(request.getParameter("electricity")));
                    }
                    if(request.getParameter("status")!=null){
                    pile.setStatus(request.getParameter("status"));
                    }
                    if(request.getParameter("createTime")!=null){
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
                    try {
                    Date date = simpleDateFormat.parse(request.getParameter("createTime"));
                    System.out.println(date);
                    pile.setCreateTime(date);
                    } catch (ParseException e) {
                    return Msg.fail().setNewMsg("日期格式有误");
                    }

                    }
                    if(request.getParameter("qrcode")!=null){
                    pile.setQrcode(request.getParameter("qrcode"));
                    }
                    if(request.getParameter("qrcodePath")!=null){
                    pile.setQrcodePath(request.getParameter("qrcodePath"));
                    }
                    if(request.getParameter("rate")!=null){
                    pile.setRate(Double.parseDouble(request.getParameter("rate")));
                    }
                    if(request.getParameter("reserve1")!=null){
                    pile.setReserve1(request.getParameter("reserve1"));
                    }
                    if(request.getParameter("reserve2")!=null){
                    pile.setReserve1(request.getParameter("reserve1"));
                    }*/