package cn.zy.charg.service;

import cn.zy.charg.bean.*;
import cn.zy.charg.dao.CaptchaMapper;
import cn.zy.charg.dao.PileMapper;
import cn.zy.charg.dao.UserMapper;
import cn.zy.charg.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CaptchaService captchaService;

    public String loginByPassword(String number,String password){
        return "1";
    }

    public User loginByCaptcha(String number,String captcha){
        if(!captcha.equals(captchaService.getCaptchaByPhoneIn5Min(number))){
            throw new BusinessException(BusinessErrorCode.USER_LOGINFAIL.setNewMessage("登录失败，验证码已失效"));
         }
        List<User> list = getUserByPhone(number);
        User user;
        if(list==null||list.size()==0){
            user = new User();
            user.setName(number);
            user.setContact(number);
            user.setAmount(0.0);
            user.setPassword(StrUtil.getRandomString(15)+"1");
            user.setReserve1("1");
            addUser(user);
            user = getUserByPhone(number).get(0);
        }else {
            user = list.get(0);
        }
        return user;
    }

    //校验验证码，手机号已存在，未存在则报错
    public User checkCaptchWithNumberExist(String number,String captcha){
        if(!captcha.equals(captchaService.getCaptchaByPhoneIn5Min(number))){
            throw new BusinessException(BusinessErrorCode.USER_LOGINFAIL.setNewMessage("验证失败，验证码已失效"));
        }
        List<User> list = getUserByPhone(number);
        if(list==null||list.size()==0){
            throw new BusinessException(BusinessErrorCode.USER_LOGINFAIL.setNewMessage("该账号未注册，请先注册"));
        }
        return list.get(0);
    }

    //校验验证码，手机号未存在，存在则报错
    public void checkCaptchWithNumberNotExist(String number,String captcha){
        if(!captcha.equals(captchaService.getCaptchaByPhoneIn5Min(number))){
            throw new BusinessException(BusinessErrorCode.USER_LOGINFAIL.setNewMessage("验证失败，验证码已失效"));
        }
        List<User> list = getUserByPhone(number);
        if(list!=null&&list.size()>0){
            throw new BusinessException(BusinessErrorCode.USER_LOGINFAIL.setNewMessage("该账号已注册，请直接使用该手机号登录"));
        }
    }


    public User loginByPwd(String number,String pwd){
        User user = getUserByPhone(number).get(0);
        if(!user.getPassword().equals(pwd)){
            throw new BusinessException(BusinessErrorCode.USER_LOGINFAIL.setNewMessage("登录失败，账号或密码错误"));
        }
        return getUserByPhone(number).get(0);
    }
    public boolean isExistUser(int uid){
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(uid);
        if(userMapper.countByExample(example)<=0){
            return false;
        }
        return true;
    }
    public List<User> getUserById(String uids){
        System.out.println(uids);
        List<Integer> idList = new ArrayList<>();
        if(uids.contains("~")){
            String[] idArray = StringUtils.split(uids,"~");
            for(String o:idArray){
                if(!StrUtil.isEmptyStr(o)){
                    System.out.println(o);
                    idList.add(Integer.parseInt(o));
                }
            }
        }else{
            idList.add(Integer.parseInt(uids));
        }
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(idList);
        List<User> list = userMapper.selectByExample(example);
        if(list.size()<=0){
            return null;
        }
        else return list;
    }


    public List<User> getUsers(){
        List<User> list = userMapper.selectByExample(null);
        if(list.size()<=0){
            return null;
        }
        else return list;
    }

    public void addUser(User user){
        if(user.getContact()==null||user.getPassword()==null){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入手机号或密码"));
        }
        //开启车牌，手机号码，密码格式正则验证
        checkUserInfo(user);
        if(getUserByPhone(user.getContact())!=null){
            throw new BusinessException(200,"该手机号已注册");
        }
        user.setId(null);
       if( userMapper.insert(user)!=1){
           throw new BusinessException(BusinessErrorCode.DB_INSERTFAIL);
       }
    }

    public List<User> getUserByPhone(String number){
        if(!StrUtil.matchString(number,StrUtil.REGEX_PHONENUMBER)){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("该手机号格式错误"));
        }
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andContactEqualTo(number);
        List<User> users = userMapper.selectByExample(example);
        if(users.size()<=0){
            return null;
        }
        return users;
    }

    public List<User> getUserByName(String name){
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(name);
        List<User> users = userMapper.selectByExample(example);
        if(users.size()<=0){
            return null;
        }
        return users;
    }

    public void updateUser(User user){
        if(!isExistUser(user.getId())){
            throw new BusinessException(BusinessErrorCode.DB_EMPTYRESULT);
        }
        User last_user = getUserById(""+user.getId()).get(0);
        checkUserInfo(user);
        if(!StrUtil.isEmptyStr(user.getContact())&&!last_user.getContact().equals(user.getContact())){
            if(getUserByPhone(user.getContact())!=null){
                throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("该手机号已存在"));
            }
        }

        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(user.getId());
        if(userMapper.updateByExampleSelective(user,example)!=1){
            throw new BusinessException(BusinessErrorCode.DB_UPDATEFAIL);
        }
    }

    public void checkUserInfo(User user){
        if(user.getPassword()!=null&&!StrUtil.matchString(user.getPassword(),StrUtil.REGEX_PASSWORD)){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("密码格式错误"));
        }
        if(user.getContact()!=null&&!StrUtil.matchString(user.getContact(),StrUtil.REGEX_PHONENUMBER)){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("手机号码格式出错"));
        }
        if(user.getLicense()!=null&&!StrUtil.matchString(user.getLicense(),StrUtil.REGEX_LICENSE)){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("车牌信息错误"));
        }
    }
    public void deleteUser(String id){
        List<Integer> idList = new ArrayList<>();
        String[] idArray = id.split("~");
        for(String o:idArray){
            if(!StrUtil.isEmptyStr(o)&&isExistUser(Integer.parseInt(o))){
                deleteSingleUser(Integer.parseInt(o));
            }
        }
    }

    public void deleteSingleUser(int id){
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);
        if(userMapper.deleteByExample(example)!=1){
            throw new BusinessException(BusinessErrorCode.DB_DELETEFAIL);
        }
    }

    public List<User> selectByThink(String word){
        if(StrUtil.isEmptyStr(word)){
           throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入联想词"));
        }
        return userMapper.selectByThink(word);
    }

    public List<User> selectByThinkWithPhone(String word){
        if(StrUtil.isEmptyStr(word)){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入联想词"));
        }
        return userMapper.selectByThinkWithPhone(word);
    }


}
