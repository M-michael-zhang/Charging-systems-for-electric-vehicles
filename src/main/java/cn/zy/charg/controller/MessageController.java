package cn.zy.charg.controller;

import cn.zy.charg.bean.*;
import cn.zy.charg.service.ManagerService;
import cn.zy.charg.service.MessageService;
import cn.zy.charg.service.UserService;
import cn.zy.charg.service.WebSocketServer;
import cn.zy.charg.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RequestMapping("/message")
@RestController
@Transactional
@CrossOrigin
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private ManagerService managerService;

    @RequestMapping("/getMessages")
    public Msg getMessages(){
        List<Message> list = messageService.getAll();
        return Msg.success().add("messages",list);
    }
    @RequestMapping("/sendMessage")
    public Msg addMessage(HttpServletRequest request){
        Message message = new Message();
        if(StrUtil.isEmptyStr(request.getParameter("uid"))||
                StrUtil.isEmptyStr(request.getParameter("content"))){
            throw new BusinessException(BusinessErrorCode.DB_INSERTFAIL.setNewMessage("请输入用户id和消息内容"));
        }
        message.setUid(Integer.parseInt(request.getParameter("uid")));
        message.setContent(request.getParameter("content"));
        //发送给客服，不清楚是哪个客服阅读，所以不设置mid，当客服阅读后会更新该消息成已读
        message.setType("0");
        messageService.addMessage(message);
        User user;
        try{
            user = userService.getUserById(request.getParameter("uid")).get(0);
        }catch (Exception e){
            throw new BusinessException(BusinessErrorCode.DB_EMPTYRESULT.setNewMessage("该用户不存在"));
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("uid",message.getUid());
            jsonObject.put("content",message.getContent());
            jsonObject.put("uname",user.getName());
            WebSocketServer.sendInfo(jsonObject.toJSONString(),"0");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Msg.success().setNewMsg("发送成功").add("content",message.getContent());
    }
    @RequestMapping("/replyMessage")
    public Msg replyMessage(HttpServletRequest request){
        Message message = new Message();
        if(StrUtil.isEmptyStr(request.getParameter("uid"))||
                StrUtil.isEmptyStr(request.getParameter("content"))||
                StrUtil.isEmptyStr(request.getParameter("mid"))){
            throw new BusinessException(BusinessErrorCode.DB_INSERTFAIL.setNewMessage("请输入用户id和消息内容"));
        }
        message.setUid(Integer.parseInt(request.getParameter("uid")));
        message.setMid(Integer.parseInt(request.getParameter("mid")));
        message.setContent(request.getParameter("content"));
        message.setType("1");
        messageService.addMessage(message);
        String mname = managerService.getManagerById(Integer.parseInt(request.getParameter("mid"))).getName();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mid",message.getMid());
            jsonObject.put("content",message.getContent());
            jsonObject.put("mname",mname);
            WebSocketServer.sendInfo(jsonObject.toJSONString(),""+message.getUid());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Msg.success();
    }

    @RequestMapping("/readMessageForUser")
    public Msg readMessageForUser(HttpServletRequest request){
        if(StrUtil.isEmptyStr(request.getParameter("uid"))){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入用户id"));
        }
        int uid = Integer.parseInt(request.getParameter("uid"));
        List<Message> list = messageService.getUnReadMessageForUser(uid);
        for(Message o : list){
            o.setReserve1("1");
            messageService.updateMessage(o);
        }
        return Msg.success();
    }

    //将未读变已读
    @RequestMapping("/readMessageForManager")
    public Msg readMessageForManager(HttpServletRequest request){
        if(StrUtil.isEmptyStr(request.getParameter("uid"))||StrUtil.isEmptyStr(request.getParameter("mid"))){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入用户id和管理员id"));
        }
        int uid = Integer.parseInt(request.getParameter("uid"));
        int mid = Integer.parseInt(request.getParameter("mid"));
        List<Message> list = messageService.getUnReadMessageForManager(uid);
        for(Message o:list){
            o.setReserve1("1");
            o.setMid(mid);
            messageService.updateMessage(o);
        }
        return Msg.success();
    }

    @RequestMapping("/getMessageByUid")
    public Msg getMessageByUid(HttpServletRequest request){
        if(StrUtil.isEmptyStr(request.getParameter("uid"))){
            throw new BusinessException(BusinessErrorCode.DB_INSERTFAIL.setNewMessage("请输入用户id"));
        }
        int uid = Integer.parseInt(request.getParameter("uid"));
        List<Message> list = messageService.getMessageByUid(uid);
        return Msg.success().add("UserId",uid).add("message",list);
    }

    @RequestMapping("/getUnReadUser")
    public Msg getUnReadUser(HttpServletRequest request){
        return Msg.success().add("users",messageService.getUnReadUser());
    }
}
