package cn.zy.charg.controller;

import cn.zy.charg.bean.Msg;
import cn.zy.charg.service.MqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.messaging.Message;
import javax.servlet.http.HttpServletRequest;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.integration.mqtt.support.MqttHeaders;

@RequestMapping("/mqtt")
@RestController
@Transactional
@CrossOrigin
public class MqttController {
    @Autowired
    private MqttService mqttService;

    @RequestMapping("/sendMqttMessage")
    public Msg sendMqttMessage(HttpServletRequest request){

        String content = request.getParameter("content");

        //发送的消息
        Message message = MessageBuilder.withPayload(content).setHeader(MqttHeaders.TOPIC, "topic1").build();
        mqttService.sendMessage(message);
        return Msg.success();

    }
}
