package cn.zy.charg.controller;

import cn.zy.charg.bean.BusinessErrorCode;
import cn.zy.charg.bean.BusinessException;
import cn.zy.charg.bean.Manager;
import cn.zy.charg.bean.Msg;
import cn.zy.charg.service.ManagerService;
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
import java.util.List;

@RequestMapping("/manager")
@RestController
@Transactional
@CrossOrigin
public class ManagerController {
    @Autowired
    private ManagerService managerService;

    @RequestMapping("/loginByPhone")
    public Msg loginByPhone(HttpServletRequest request){
        if(StrUtil.isEmptyStr(request.getParameter("contact"))||StrUtil.isEmptyStr(request.getParameter("password"))){
            throw new BusinessException(BusinessErrorCode.USER_LOGINFAIL.setNewMessage("登录失败，请输入手机号和密码"));
        }
        Manager manager = managerService.getManagerByPhone(request.getParameter("contact").trim());
        if(!manager.getPassword().equals(request.getParameter("password").trim())){
            return Msg.fail();
        }
        return Msg.success().add("manager",manager);
    }

    @RequestMapping("/login")
    public Msg login(HttpServletRequest request){
        if(StrUtil.isEmptyStr(request.getParameter("id"))||StrUtil.isEmptyStr(request.getParameter("password"))){
            throw new BusinessException(BusinessErrorCode.USER_LOGINFAIL.setNewMessage("登录失败，请输入id和密码"));
        }
        Manager manager = managerService.getManagerById(Integer.parseInt(request.getParameter("id").trim()));
        if(manager == null || !manager.getPassword().equals(request.getParameter("password").trim())){
            return Msg.fail().setNewMsg("登录失败，账号或密码错误");
        }
        manager.setPassword("***********");
        return Msg.success().add("Manager",manager);
    }

    @RequestMapping("/getAll")
    public Msg getAll(@RequestParam(value = "pn",defaultValue = "1")Integer pn, Model model,
                      @RequestParam(value = "pz",defaultValue = "10")Integer pz){
        PageHelper.startPage(pn,pz);
        List<Manager> list = managerService.getAll();
        PageInfo page = new PageInfo(list,5);
        model.addAttribute("PageInfo",page);
        return Msg.success().add("Managers",page);
//        for(Manager o : list){
//            o.setPassword("**********");
//        }
    }

    @RequestMapping("/getManager")
    public Msg getManager(HttpServletRequest request){
        if(StrUtil.isEmptyStr(request.getParameter("id"))){
            throw new BusinessException(BusinessErrorCode.USER_LOGINFAIL.setNewMessage("请输入id"));
        }
        Manager manager =managerService.getManagerById(Integer.parseInt(request.getParameter("id")));
        if(manager!=null){
            manager.setPassword("***********");
            return Msg.success().add("Manager",manager);
        }
        return Msg.fail();
    }

    @RequestMapping("/addManager")
    public Msg addManager(HttpServletRequest request){
        Manager manager = new Manager();
        manager.setPassword(request.getParameter("password"));
        manager.setContact(request.getParameter("contact"));
        manager.setDepartment(request.getParameter("department"));
        manager.setName(request.getParameter("name"));
        manager.setPermission(request.getParameter("permission"));
        manager.setReserve1(request.getParameter("reserve1"));
        manager.setReserve2(request.getParameter("reserve2"));
        managerService.addManager(manager);
        return Msg.success();
    }

    @RequestMapping("/deleteManager")
    public Msg deleteManager(HttpServletRequest request){
        if(StrUtil.isEmptyStr(request.getParameter("id"))){
            throw new BusinessException(BusinessErrorCode.DB_DELETEFAIL.setNewMessage("请输入id"));
        }
        managerService.deleteManager(Integer.parseInt(request.getParameter("id")));
        return Msg.success();
    }

    @RequestMapping("/updateManager")
    public Msg updateManager(HttpServletRequest request){
        Manager manager = new Manager();
        if(StrUtil.isEmptyStr(request.getParameter("id"))){
            throw new BusinessException(BusinessErrorCode.DB_UPDATEFAIL.setNewMessage("请输入id"));
        }
        manager.setId(Integer.parseInt(request.getParameter("id")));
        manager.setPassword(request.getParameter("password"));
        manager.setContact(request.getParameter("contact"));
        manager.setDepartment(request.getParameter("department"));
        manager.setName(request.getParameter("name"));
        manager.setPermission(request.getParameter("permission"));
        manager.setReserve1(request.getParameter("reserve1"));
        manager.setReserve2(request.getParameter("reserve2"));
        managerService.updateManager(manager);
        return Msg.success();
    }

    @RequestMapping("/selectByThinkWithPage")
    public Msg selectByThinkWithPage(@RequestParam(value = "pn",defaultValue = "1")Integer pn, Model model, String word,
                                     @RequestParam(value = "pz",defaultValue = "10")Integer pz){
        PageHelper.startPage(pn,pz);
        List<Manager> list = managerService.selectByThink(word);
        if(list==null){
            return Msg.fail().setNewMsg("该用户不存在");
        }
        PageInfo page = new PageInfo(list,5);
        model.addAttribute("PageInfo",page);
        return Msg.success().add("Managers",page);
    }
}
