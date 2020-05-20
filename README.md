# Uav-charging-pile-system
电动汽车充电系统后台实现  
**功能介绍图**
![功能设计图](https://raw.githubusercontent.com/M-michael-zhang/Uav-charging-pile-system/master/show/function.png)  
管理端(web)实现地址:   
用户端(Android)实现地址:  

## 技术栈：
* **SpringBoot+Hibernate** 实现请求与数据库操作
* **Pagehelper**  实现应答数据分页
* **Aliyun Java SDK** 实现发送短信
* **Emqx** 搭建MQTT服务器
* **Javax.websocket websocke** 处理用户与管理员通信
* **Paho.mqtt** mqtt处理充电桩与服务器通信

## 实现效果
### 标志物展示与导航
![地图与导航](https://raw.githubusercontent.com/M-michael-zhang/Uav-charging-pile-system/master/show/map.gif)
### 短信登录
![短信登录](https://raw.githubusercontent.com/M-michael-zhang/Uav-charging-pile-system/master/show/sms_login.gif)
### 聊天
![聊天](https://raw.githubusercontent.com/M-michael-zhang/Uav-charging-pile-system/master/show/chat.gif)
### 远程断电
![远程断电](https://raw.githubusercontent.com/M-michael-zhang/Uav-charging-pile-system/master/show/remotePowerOff.gif)
