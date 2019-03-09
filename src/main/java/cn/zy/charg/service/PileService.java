package cn.zy.charg.service;

import cn.zy.charg.bean.Pile;
import cn.zy.charg.dao.PileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PileService {
    @Autowired
    private PileMapper pileMapper;

    public List<Pile> getAll(){
        return pileMapper.selectByExample(null);
    }

    public Pile getPile(Integer id){
        return pileMapper.selectByPrimaryKey(id);
    }

    public void savePile(Pile pile){
        pileMapper.insertSelective(pile);
    }

    public void updatePile(Pile pile){
        pileMapper.updateByPrimaryKeySelective(pile);
    }
    public void deletePile(Integer id){
        pileMapper.deleteByPrimaryKey(id);
    }

}
