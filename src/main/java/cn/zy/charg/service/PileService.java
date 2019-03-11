package cn.zy.charg.service;

import cn.zy.charg.bean.Pile;
import cn.zy.charg.bean.PileExample;
import cn.zy.charg.dao.PileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PileService {

    @Autowired
    private PileMapper pileMapper;

    public List<Pile> getAll(){
        return pileMapper.selectByExample(null);
    }

    public Pile getPile(Integer id){
        PileExample example = new PileExample();
        PileExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);
        List<Pile> list = pileMapper.selectByExample(example);
        if(list.size()==0){
            return null;
        }
        else return list.get(0);
    }
//
    public void savePile(Pile pile){
        pileMapper.insertSelective(pile);
    }
//
//    public void updatePile(Pile pile){
//        pileMapper.updateByPrimaryKeySelective(pile);
//    }
//    public void deletePile(Integer id){
//        pileMapper.deleteByPrimaryKey(id);
//    }

}
