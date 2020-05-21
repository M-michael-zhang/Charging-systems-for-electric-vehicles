# Charging-systems-for-electric-vehicles
电动汽车充电系统后台实现  (由于毕设答辩完成，剩下的坑不想填了，代码不够完善请见谅）
**功能介绍图**
![功能设计图](https://raw.githubusercontent.com/M-michael-zhang/Uav-charging-pile-system/master/show/function.png)  
管理端(web)实现地址: https://github.com/M-michael-zhang/Management-end-of-charging-system  
用户端(Android)实现地址:  https://github.com/M-michael-zhang/Client-of-charging-system

## 技术栈：
* **SpringBoot+Hibernate** 实现请求与数据库操作
* **Pagehelper**  实现应答数据分页
* **Aliyun Java SDK** 实现发送短信
* **Emqx** 搭建MQTT服务器
* **Javax.websocket websocke** 处理用户与管理员通信
* **Paho.mqtt** mqtt处理充电桩与服务器通信

## 实现效果
***标志物展示与导航***
![地图与导航](https://raw.githubusercontent.com/M-michael-zhang/Uav-charging-pile-system/master/show/map.gif)
***短信登录***
![短信登录](https://raw.githubusercontent.com/M-michael-zhang/Uav-charging-pile-system/master/show/sms_login.gif)
***聊天***
![聊天](https://raw.githubusercontent.com/M-michael-zhang/Uav-charging-pile-system/master/show/chat.gif)
***远程断电***
![远程断电](https://raw.githubusercontent.com/M-michael-zhang/Uav-charging-pile-system/master/show/remotePowerOff.gif)

## 使用过程
* 将项目导入Idea
* 系统使用的MySQL数据库，将sqldata中的sql文件导入至数据库，并在src/main/resources/application.yml中配置数据库信息，若需要自定义数据库，本系统使用hibernate自动生成代码，请在mgb.xml中的配置数据库信息和数据表(已有的表请注释），并运行src/main/java/cn/zy/charge/(该路径下文直接省去）test/MGBtest.java，将自动生成相应bean和mapper文件。
* 若使用短信服务，请在阿里云的短信服务申请AccessKey ID和AccessKey Secret，并申请短信格式，在/util/SmsUtil中配置短信信息
* 启动/ChargApplication.java即可运行后台
* 测试 http://localhost:8088/pile/getPiles 是否获取所有充电桩信息
## MQTT代理服务器搭建
* sqldata中有emqx的windows和centos的安装包，本人只使用过centos，设置过程如下
* 将rpm上传至服务器并安装，使用 ```sudo emqx start``` 命令启动
* 访问 http://服务器IP:18083  , 用户名admin 密码 public 
* 设置项目的src/main/resources/application.yml中配置mqtt的url的ip
