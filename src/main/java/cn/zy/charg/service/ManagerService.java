package cn.zy.charg.service;

import cn.zy.charg.bean.BusinessErrorCode;
import cn.zy.charg.bean.BusinessException;
import cn.zy.charg.bean.Manager;
import cn.zy.charg.bean.ManagerExample;
import cn.zy.charg.dao.ManagerMapper;
import cn.zy.charg.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ManagerService {
    @Autowired
    private ManagerMapper managerMapper;

    public List<Manager> getAll(){
        return managerMapper.selectByExample(null);
    }

    public void addManager(Manager manager){
        if(StrUtil.isEmptyStr(manager.getContact())||
                StrUtil.isEmptyStr(manager.getName())||
                StrUtil.isEmptyStr(manager.getPassword())){
            throw  new BusinessException(BusinessErrorCode.DB_INSERTFAIL.setNewMessage("数据缺失，请输入联系方式，姓名和密码"));
        }
        if(!StrUtil.matchString(manager.getContact(),StrUtil.REGEX_PHONENUMBER)){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("手机号格式不正确"));
        }
        if(isExistManagerByPhone(manager.getContact())){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("该手机号已存在"));
        }
        manager.setId(null);
        manager.setCreateTime(new Date());
        if(managerMapper.insert(manager)!=1){
            throw  new BusinessException(BusinessErrorCode.DB_INSERTFAIL);
        }
    }

    public void deleteManager(int id){
        if(!isExistManager(id)){
            throw new BusinessException(BusinessErrorCode.DB_DELETEFAIL.setNewMessage("该管理员不存在"));
        }
        ManagerExample example = new ManagerExample();
        ManagerExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);
        if(managerMapper.deleteByExample(example)!=1){
            throw  new BusinessException(BusinessErrorCode.DB_DELETEFAIL);
        }
    }

    public Manager getManagerById(int id){
        ManagerExample example = new ManagerExample();
        ManagerExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);
        List<Manager> list = managerMapper.selectByExample(example);
        if(list==null||list.size()<=0){
            return null;
        }
        return list.get(0);
    }

    public void updateManager(Manager manager){
        if(!isExistManager(manager.getId())){
            throw new BusinessException(BusinessErrorCode.DB_EMPTYRESULT);
        }
        ManagerExample example = new ManagerExample();
        ManagerExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(manager.getId());
        if(managerMapper.updateByExampleSelective(manager,example)!=1){
            throw new BusinessException(BusinessErrorCode.DB_UPDATEFAIL);
        }
    }

    public boolean isExistManager(int id){
        ManagerExample example = new ManagerExample();
        ManagerExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);
        long count = managerMapper.countByExample(example);
        if(count<=0){
            return false;
        }
        return true;
    }

    public boolean isExistManagerByPhone(String number){
        ManagerExample example = new ManagerExample();
        ManagerExample.Criteria criteria = example.createCriteria();
        criteria.andContactEqualTo(number);
        long count = managerMapper.countByExample(example);
        if(count<=0){
            return false;
        }
        return true;
    }

    public Manager getManagerByPhone(String number){
        if(!isExistManagerByPhone(number)){
            throw new BusinessException(BusinessErrorCode.DB_EMPTYRESULT.setNewMessage("该手机号不存在"));
        }
        ManagerExample example = new ManagerExample();
        ManagerExample.Criteria criteria = example.createCriteria();
        criteria.andContactEqualTo(number);
        return managerMapper.selectByExample(example).get(0);
    }
    public List<Manager> selectByThink(String word){
        if(StrUtil.isEmptyStr(word)){
            throw new BusinessException(BusinessErrorCode.ERROR_DEFAULT.setNewMessage("请输入联想词"));
        }
        return managerMapper.selectByThink(word);
    }
}
