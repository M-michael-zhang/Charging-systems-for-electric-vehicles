package cn.zy.charg.service;

import cn.zy.charg.bean.BusinessErrorCode;
import cn.zy.charg.bean.BusinessException;
import cn.zy.charg.bean.Trans;
import cn.zy.charg.bean.TransExample;
import cn.zy.charg.dao.TransMapper;
import cn.zy.charg.util.DateUtil;
import cn.zy.charg.util.DoubleUtil;
import cn.zy.charg.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class TransService {

    @Autowired
    private TransMapper transMapper;

    @Autowired
    private PileService pileService;

    private TransExample example = new TransExample();
    TransExample.Criteria criteria = example.createCriteria();

    public List<Trans> getAll(){
        return transMapper.selectByExample(null);
    }
    public void addTrans(Trans trans){
        trans.setTransTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String dateStr = sdf.format(trans.getTransTime());
        trans.setSerialNo(dateStr);
        if(transMapper.insert(trans)!=1){
            throw new BusinessException(BusinessErrorCode.DB_INSERTFAIL);
        }


    }
    public List<Trans> getDoingTrans(int uid){
        TransExample example = new TransExample();
        TransExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andStatusEqualTo("0");
        example.setOrderByClause("serial_no desc");
        List<Trans> list = transMapper.selectByExample(example);
        if(list.size()<=0){
            return null;
        }
        return list;
    }
    public List<Trans> getTransByUid(int id){
        TransExample example = new TransExample();
        TransExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(id);
        example.setOrderByClause("trans_time desc");
        List<Trans> list = transMapper.selectByExample(example);
        if(list==null||list.size()<=0){
            return null;
        }
        return list;
    }
    public List<Trans> getTransSelective(Map<String,String[]> param){
        TransExample example = new TransExample();
        TransExample.Criteria criteria = example.createCriteria();
        if(param.get("serial")!=null){
            criteria.andSerialNoEqualTo(StrUtil.getArrayToString(param.get("serial")));
        }
        //transDate格式20200202
        if(param.get("transDate")!=null){
            String transDate = StrUtil.getArrayToString(param.get("transDate"));
            criteria.andTransTimeBetween(DateUtil.getBeginTimeFromDate(transDate),DateUtil.getEndTimeFromDate(transDate));
        }
        if(param.get("uid")!=null){
            criteria.andUidEqualTo(Integer.parseInt(StrUtil.getArrayToString(param.get("uid"))));
        }
        if(param.get("pid")!=null){
            criteria.andPidEqualTo(Integer.parseInt(StrUtil.getArrayToString(param.get("pid"))));
        }
        if(param.get("type")!=null){
            criteria.andTypeEqualTo(StrUtil.getArrayToString(param.get("type")));
        }
        if(param.get("status")!=null){
            criteria.andStatusEqualTo(StrUtil.getArrayToString(param.get("status")));
        }
        List<Trans> trans = transMapper.selectByExample(example);
        return trans;
    }
    public void updateTrans(Trans trans){
        if(StrUtil.isEmptyStr(trans.getSerialNo())){
            throw new BusinessException(BusinessErrorCode.DB_UPDATEFAIL);
        }
        TransExample example = new TransExample();
        TransExample.Criteria criteria = example.createCriteria();
        criteria.andSerialNoEqualTo(trans.getSerialNo());
        if(transMapper.updateByExample(trans,example)!=1){
            throw new BusinessException(BusinessErrorCode.DB_UPDATEFAIL);
        }
    }

    public Trans getTransInBooking(int uid,int pid){
        TransExample example = new TransExample();
        TransExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andPidEqualTo(pid);
        criteria.andStatusEqualTo("0");
        criteria.andTypeEqualTo("0");
        List<Trans> list = transMapper.selectByExample(example);
        if(list.size()<=0){
            return null;
        }
        return list.get(0);
    }

    public Trans getTrans(String serial){
        TransExample example = new TransExample();
        TransExample.Criteria criteria = example.createCriteria();
        criteria.andSerialNoEqualTo(serial);
        List<Trans> list = transMapper.selectByExample(example);
        if(list.size()<=0){
            return null;
        }
        Trans trans = list.get(0);
        updateTrans(trans);
        return trans;
    }
    public Trans getTransInUsing(int uid,int pid){
        TransExample example = new TransExample();
        TransExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andPidEqualTo(pid);
        criteria.andStatusEqualTo("0");
        criteria.andTypeEqualTo("1");
        List<Trans> list = transMapper.selectByExample(example);
        System.out.println(list.get(0).getSerialNo());
        if(list.size()<=0){
            return null;
        }
        return list.get(0);
    }
    public void deleteTrans(String serial){
        if(StrUtil.isEmptyStr(serial)||!isExistTrans(serial)){
            throw new BusinessException(BusinessErrorCode.DB_DELETEFAIL);
        }
        TransExample example = new TransExample();
        TransExample.Criteria criteria = example.createCriteria();
        criteria.andSerialNoEqualTo(serial);
        if(transMapper.deleteByExample(example)!=1){
            throw new BusinessException(BusinessErrorCode.DB_DELETEFAIL);
        }
    }

    public boolean isExistTrans(String serial){
        TransExample example = new TransExample();
        TransExample.Criteria criteria = example.createCriteria();
        criteria.andSerialNoEqualTo(serial);
        long count = transMapper.countByExample(example);
        if(count<=0) return false;
        return true;
    }
     public void finishTrans(int pid){
         TransExample example = new TransExample();
         TransExample.Criteria criteria = example.createCriteria();
         criteria.andPidEqualTo(pid);
         criteria.andStatusEqualTo("0");
         Trans trans;
         if(transMapper.selectByExample(example).size()<0){
             throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("该流水号不存在"));
         }else if(!transMapper.selectByExample(example).get(0).getStatus().equals("0")){
             throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("该交易已结束"));
         }else{
             trans = transMapper.selectByExample(example).get(0);
             trans.setStatus("1");
             trans.setEndTime(new Date());
             Long l = (trans.getEndTime().getTime()-trans.getTransTime().getTime());
             trans.setCostTime(DoubleUtil.getDecimalTo2(l.doubleValue()/3600000));
             if(trans.getType().equals("1")){
                 double amount = pileService.getPile(trans.getPid()).getRate()*(l.doubleValue()/3600000);
                 trans.setAmount(DoubleUtil.getDecimalTo2(amount));
             }
             updateTrans(trans);
         }
     }


}
