package cn.zy.charg.controller;

import cn.zy.charg.bean.BusinessErrorCode;
import cn.zy.charg.bean.BusinessException;
import cn.zy.charg.bean.Msg;
import cn.zy.charg.bean.User;
import cn.zy.charg.service.CaptchaService;
import cn.zy.charg.service.UserService;
import cn.zy.charg.util.SmsUtil;
import cn.zy.charg.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/user")
@RestController
@Transactional
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private CaptchaService captchaService;

    @RequestMapping("/loginByCaptcha")
    public Msg loginByCaptcha(HttpServletRequest request){
        if(StrUtil.isEmptyStr(request.getParameter("number"))||StrUtil.isEmptyStr(request.getParameter("captcha"))){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入手机号和验证码"));
        }
        String number =request.getParameter("number");
        String captcha = request.getParameter("captcha");
        User user = userService.loginByCaptcha(number,captcha);
        return Msg.success().setNewMsg("登录成功").add("user",user);
    }
    //修改手机号 验证码(若手机号已存在则报错）
    @RequestMapping("/checkCaptchaWithChangeNumber")
    public Msg checkCaptcha(HttpServletRequest request){
        if(StrUtil.isEmptyStr(request.getParameter("number"))||StrUtil.isEmptyStr(request.getParameter("captcha"))){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入手机号和验证码"));
        }
        String number =request.getParameter("number");
        String captcha = request.getParameter("captcha");
        userService.checkCaptchWithNumberNotExist(number,captcha);
        return Msg.success();
    }
    //修改密码 验证码（若手机号不存在则报错）
    @RequestMapping("/checkCaptchaWithChangePassword")
    public Msg checkCaptchaWithChangePassword(HttpServletRequest request){
        if(StrUtil.isEmptyStr(request.getParameter("number"))||StrUtil.isEmptyStr(request.getParameter("captcha"))){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入手机号和验证码"));
        }
        String number =request.getParameter("number");
        String captcha = request.getParameter("captcha");
        userService.checkCaptchWithNumberExist(number,captcha);
        return Msg.success();
    }


    @RequestMapping("/login")
    public Msg login(HttpServletRequest request){
        if(StrUtil.isEmptyStr(request.getParameter("number"))||StrUtil.isEmptyStr(request.getParameter("password"))){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入手机号和密码"));
        }
        String number =request.getParameter("number");
        String pwd = request.getParameter("password");
        User user = userService.loginByPwd(number,pwd);
        return Msg.success().setNewMsg("登录成功").add("user",user);
    }
    @RequestMapping("/sendCaptcha")
    public Msg sendCaptcha(HttpServletRequest request){
        if(StrUtil.isEmptyStr(request.getParameter("number"))){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入发送手机号"));
        }
        String number =request.getParameter("number");
        String captcha = ""+ (int)((Math.random()*9+1)*100000);
        if(SmsUtil.sendLoginSms(number,captcha)){
//        if(true){
            captchaService.addCaptcha(number,captcha);
            return Msg.success();
        }
        return Msg.fail();
    }

    @RequestMapping("/getUsers")
    @ResponseBody
    public Msg getUsers(@RequestParam(value = "pn",defaultValue = "1")Integer pn, Model model,
                        @RequestParam(value = "pz",defaultValue = "10")Integer pz){
        PageHelper.startPage(pn,pz);
        List<User> list = userService.getUsers();
        PageInfo page = new PageInfo(list,5);
        model.addAttribute("PageInfo",page);
        return Msg.success().add("Users",page);
        //return Msg.success().add("Users",list).add("Count",list.size());
    }


    @RequestMapping("/addUser")
    public Msg addUser(HttpServletRequest request){
        User user = buildUserFromRequset(request);
        userService.addUser(user);
        return Msg.success().setNewMsg("新增用户成功").add("user",user);
    }
//    @RequestMapping("/getUserById")
//    public Msg getUserById(HttpServletRequest request){
//        if(StrUtil.isEmptyStr(request.getParameter("id"))){
//            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT);
//        }
//        String id = request.getParameter("id");
//        System.out.println("***************");
//        List<User> users = userService.getUserById(id);
//        if(users==null){
//            return Msg.fail().setNewMsg("该用户不存在");
//        }
//        Map<String, Object> map = new HashMap<>();
//        map.put("list",users);
//        map.put("Count", users.size());
//        return Msg.success().add("Users",map);
//    }

    @RequestMapping("/getUserById")
    public Msg getUserById(@RequestParam(value = "pn",defaultValue = "1")Integer pn, Model model,String id,
                           @RequestParam(value = "pz",defaultValue = "10")Integer pz){
        if(StrUtil.isEmptyStr(id)){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入用户id"));
        }
        PageHelper.startPage(pn,pz);
        List<User> users = userService.getUserById(id);
        if(users==null){
            return Msg.fail().setNewMsg("该用户不存在");
        }
        //return Msg.success().add("Users",users).add("Count",users.size());
        PageInfo page = new PageInfo(users,5);
        model.addAttribute("PageInfo",page);
        return Msg.success().add("Users",page);
    }

    @RequestMapping("/getUserByPhone")
    public Msg getUserByPhone(@RequestParam(value = "pn",defaultValue = "1")Integer pn, Model model,String number,
                              @RequestParam(value = "pz",defaultValue = "10")Integer pz){
        if(StrUtil.isEmptyStr(number)){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入用户手机号"));
        }
        PageHelper.startPage(pn,pz);
        List<User> list = userService.getUserByPhone(number);
        if(list==null){
            return Msg.fail().setNewMsg("该用户不存在");
        }
        PageInfo page = new PageInfo(list,5);
        model.addAttribute("PageInfo",page);
        return Msg.success().add("Users",page);
        //return Msg.success().add("Users",list).add("Count",list.size());
    }

    @RequestMapping("/getUserByName")
    public Msg getUserByName(@RequestParam(value = "pn",defaultValue = "1")Integer pn, Model model,String name,
                             @RequestParam(value = "pz",defaultValue = "10")Integer pz){
        if(StrUtil.isEmptyStr(name)){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入用户名"));
        }
        PageHelper.startPage(pn,pz);
        List<User> list = userService.getUserByName(name);
        if(list==null){
            return Msg.fail().setNewMsg("该用户不存在");
        }
        PageInfo page = new PageInfo(list,5);
        model.addAttribute("PageInfo",page);
        return Msg.success().add("Users",page);
    }

    @RequestMapping("/deleteUser")
    public Msg deleteUser(HttpServletRequest request){
        if(StrUtil.isEmptyStr(request.getParameter("id"))){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入用户id"));
        }
        userService.deleteUser(request.getParameter("id"));
        return Msg.success();
    }

    //@RequestMapping("/deleteUsers")
    public Msg deleteUsers(HttpServletRequest request){
        String ids = request.getParameter("id");
        //待新增
        return Msg.success();
    }

    @RequestMapping("/updateUser")
    public Msg updateUsers(HttpServletRequest request){
        User user = buildUserFromRequset(request);
        if(user.getId()==null){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入用户id"));
        }
        userService.updateUser(user);
        return Msg.success();
    }
    @RequestMapping("/selectByThinkWithPage")
    public Msg selectByThinkWithPage(@RequestParam(value = "pn",defaultValue = "1")Integer pn, Model model,String word,
                           @RequestParam(value = "pz",defaultValue = "10")Integer pz){
        PageHelper.startPage(pn,pz);
        List<User> list = userService.selectByThinkWithPhone(word);
        if(list==null){
            return Msg.fail().setNewMsg("该用户不存在");
        }
        PageInfo page = new PageInfo(list,5);
        model.addAttribute("PageInfo",page);
        return Msg.success().add("Users",page);
    }

    @RequestMapping("/selectByThink")
    public Msg selectByThink(String word){
        return Msg.success().add("User",userService.selectByThink(word));
    }


    public User buildUserFromRequset(HttpServletRequest request){
        User user = new User();
        try{
            if(!StrUtil.isEmptyStr(request.getParameter("id"))){
                user.setId(Integer.parseInt(request.getParameter("id")));
            }
            user.setName(request.getParameter("name"));
            user.setPassword(request.getParameter("password"));
            user.setContact(request.getParameter("contact"));
            user.setReserve1(request.getParameter("reserve1"));
            user.setReserve2(request.getParameter("reserve2"));
            if(!StrUtil.isEmptyStr(request.getParameter("license"))){
                user.setLicense(request.getParameter("license"));
            }
            if(!StrUtil.isEmptyStr(request.getParameter("amount"))){
                user.setAmount(Double.parseDouble(request.getParameter("amount")));
            }
        } catch (NumberFormatException e){
            e.printStackTrace();
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("格式转换失败"));
        } catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT);
        }
        return user;
    }

}
