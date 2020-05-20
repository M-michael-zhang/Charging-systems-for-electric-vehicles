package cn.zy.charg.service;

import cn.zy.charg.util.StrUtil;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.zy.charg.dao.*;
import cn.zy.charg.bean.*;
import org.springframework.transaction.annotation.Transactional;


import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class PileService {

    @Autowired
    private PileMapper pileMapper;

    @Autowired
    private PositionCityMapper positionCityMapper;

    @Autowired
    private MessageService messageService;

    @Autowired
    private TransService transService;

    public List<Pile> getAll(){
        return pileMapper.selectByExample(null);
    }

    public Pile getPile(Integer id){
        PileExample example = new PileExample();
        PileExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);
        List<Pile> list = pileMapper.selectByExample(example);
        if(list.size()<=0){
            return null;
        }
        else return list.get(0);
    }

    public Pile getPileByQRCode(String qrcode){
        PileExample example = new PileExample();
        PileExample.Criteria criteria = example.createCriteria();
        criteria.andQrcodeEqualTo(qrcode);
        List<Pile> list = pileMapper.selectByExample(example);
        if(list.size()<=0){
            return null;
        }
        else return list.get(0);
    }

    public boolean isExistPile(Integer id){
        PileExample example = new PileExample();
        PileExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);
        long count= pileMapper.countByExample(example);
        if(count<=0){
            return false;
        }
        return true;
    }
//
    public void addPile(Pile pile){
        if(pileMapper.insertSelective(pile)!=1){
            throw new BusinessException(BusinessErrorCode.DB_INSERTFAIL);
        }
    }

    public void updatePile (Pile pile){
        if(getPile(pile.getId())==null){
            throw new BusinessException(BusinessErrorCode.DB_EMPTYRESULT);
        }
        PileExample example = new PileExample();
        PileExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(pile.getId());
        if(pileMapper.updateByExampleSelective(pile,example)!=1){
            throw new BusinessException(BusinessErrorCode.DB_UPDATEFAIL);
        }
    }

    public void deletePile(Integer id){
        if(!isExistPile(id)){
            throw new BusinessException(BusinessErrorCode.DB_DELETEFAIL.setNewMessage("该充电桩不存在"));
        }
        PileExample example = new PileExample();
        PileExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);
        if(pileMapper.deleteByExample(example)!=1){
            throw new BusinessException(BusinessErrorCode.DB_DELETEFAIL);
        }
    }

    public void updatePileStatus(int id,String status){
        Pile pile = getPile(id);
        if(pile==null) throw new BusinessException(BusinessErrorCode.DB_EMPTYRESULT);
        pile.setStatus(status);
        updatePile(pile);


    }
    public List<Pile> getPileByPosition(String city_id){
        String temp = city_id.substring(0,4);
        List<Pile> list = pileMapper.selectPileByAddress(temp);
        if(list==null||list.size()<=0){
            throw new BusinessException(BusinessErrorCode.DB_EMPTYRESULT);
        }
        return list;
    }
    //更新充电桩插头状态
    public void updatePlugStatue(int pid,String status){
        Pile pile = getPile(pid);
        pile.setReserve2(updateReserve2(pile,status,0));
        System.out.println(status+"sssss"+pile.getStatus());
        if(status.equals("0")&&pile.getStatus().equals("1")){
            finishCharge(pid);
        }
        updatePile(pile);
        messageService.sendMqttMessage(pile);
    }

    //更新充电桩电源状态
    public void updateChargeStatue(int pid,String status){
        Pile pile = getPile(pid);
        if(status.equals("1")&&!pile.getStatus().equals("0")){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("该充电桩不可用"));
        }
        if(status.equals("0")&&pile.getStatus().equals("1")){
            finishCharge(pid);
        }
        pile.setStatus(status);
        pile.setReserve2(updateReserve2(pile,status,1));
        pile.setReserve2(updateReserve2(pile,"8",3));
        updatePile(pile);
        messageService.sendMqttMessage(pile);
    }

    /*
    * 更新充电桩充电桩充电进程
    * 第0位 电流
    * 第1位 电压
    * 第2位 进程
    * 第3位 剩余时间
    * */
    public void updateChargeProcess(int pid,String process){
        Pile pile = getPile(pid);
        String[] array = process.split("-");
        if(!StrUtil.isEmptyStr(array[0])){
            pile.setElectricity(Double.parseDouble(array[0]));
        }
        if(!StrUtil.isEmptyStr(array[1])){
            pile.setVoltage(Double.parseDouble(array[1]));
        }
        if(!StrUtil.isEmptyStr(array[2])){
            pile.setReserve2(updateReserve2(pile,array[2],2));
        }
        if(!StrUtil.isEmptyStr(array[3])){
            pile.setReserve2( updateReserve2(pile,array[3],3));
        }
        updatePile(pile);
        messageService.sendMqttMessage(pile);


    }

    public void finishCharge(int pid){
        Pile pile = getPile(pid);
        pile.setStatus("0");
        pile.setReserve2("0~0~0~0~");
        pile.setElectricity(0.0);
        pile.setVoltage(0.0);
        updatePile(pile);
        transService.finishTrans(pid);
        messageService.sendMqttMessage(pile);
    }

    public void finishChargeFromClient(int pid){
        Pile pile = getPile(pid);
        pile.setStatus("0");
        pile.setReserve2("0~0~0~0~");
        pile.setElectricity(0.0);
        pile.setVoltage(0.0);
        updatePile(pile);
        messageService.sendMqttMessage(pile);
    }

    public void pileMqttConnect(int pid){
        Pile pile = getPile(pid);
        messageService.sendMqttMessage(pile);
    }


    public String updateReserve2(Pile pile,String changeStr,int index){
        String reserve2 = pile.getReserve2();
        if(StrUtil.isEmptyStr(reserve2)){
            reserve2 = " ~ ~ ~ ";
        }
        String[] array = reserve2.split("~");
        array[index] = changeStr;
        StringBuffer sb = new StringBuffer();
        for (String s : array) {
            sb.append(s+'~');
        }
        reserve2 = sb.toString();
        return reserve2;
    }




}
