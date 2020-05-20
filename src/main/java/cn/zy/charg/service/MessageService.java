package cn.zy.charg.service;

import cn.zy.charg.bean.*;
import cn.zy.charg.dao.MessageMapper;
import cn.zy.charg.util.DateUtil;
import cn.zy.charg.util.JsonUtil;
import cn.zy.charg.util.StrUtil;


import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MqttService mqttService;

    public List<Message> getAll(){
        return messageMapper.selectByExample(null);
    }

    public void addMessage(Message message){
        //校验 不校验mid 当客服阅读后会更新该未读消息的mid
        if(StrUtil.isEmptyStr(message.getType())||StrUtil.isEmptyStr(message.getContent())||message.getUid()==null){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("信息格式缺失"));
        }
        message.setTime(new Date());
        message.setId(null);
        //初始未读 reserve1=0
        message.setReserve1("0");
        if(messageMapper.insert(message)!=1){
            throw new BusinessException(BusinessErrorCode.DB_INSERTFAIL);
        }
    }

    public void updateMessage(Message message){
        if(!isExistMessage(message.getId())){
            throw new BusinessException(BusinessErrorCode.DB_EMPTYRESULT);
        }
        MessageExample example = new MessageExample();
        MessageExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(message.getId());
        if(messageMapper.updateByExampleSelective(message,example)!=1){
            throw new BusinessException(BusinessErrorCode.DB_UPDATEFAIL);
        }
    }

    public boolean isExistMessage(int id){
        MessageExample example = new MessageExample();
        MessageExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);
        long count = messageMapper.countByExample(example);
        if(count<=0){
            return false;
        }
        return true;
    }

    public void deleteMessage(int id){
        if(!isExistMessage(id)){
            throw new BusinessException(BusinessErrorCode.DB_DELETEFAIL.setNewMessage("删除失败，该编号消息不存在"));
        }
        MessageExample example = new MessageExample();
        MessageExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);
        messageMapper.deleteByExample(example);

    }


    public List<Message> getMessageByUid(int uid){
        MessageExample example = new MessageExample();
        MessageExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        example.setOrderByClause("time asc");
        List<Message> list = messageMapper.selectByExample(example);
        if(list.size()<=0){
            return null;
        }
        return list;
    }

    //针对用户返回未阅读的消息
    public List<Message> getUnReadMessageForUser(int uid){
        MessageExample example = new MessageExample();
        MessageExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andTypeEqualTo("1");
        criteria.andReserve1EqualTo("0");
        example.setOrderByClause(" time asc ");
        return messageMapper.selectByExample(example);
    }
    //针对管理员返回未阅读的消息(当点击一项用户时)
    public List<Message> getUnReadMessageForManager(int uid){
        MessageExample example = new MessageExample();
        MessageExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andTypeEqualTo("0");
        criteria.andReserve1EqualTo("0");
        example.setOrderByClause(" time asc ");
        return messageMapper.selectByExample(example);
    }

    //管理员打开聊天页面，显示有未读消息的用户
    public List<Map<String, Object>> getUnReadUser(){
        return messageMapper.selectUnReadUser();
    }

    public void sendMqttMessage(String content){
        org.springframework.messaging.Message message = MessageBuilder.withPayload(content).setHeader(MqttHeaders.TOPIC, "topic2").build();
        mqttService.sendMessage(message);
    }
    public void sendMqttMessage(Pile pile){
        String object = JsonUtil.toFastJson(pile);
        org.springframework.messaging.Message message = MessageBuilder.withPayload(object).setHeader(MqttHeaders.TOPIC, "topic2").build();
        mqttService.sendMessage(message);
    }

}
