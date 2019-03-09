package cn.zy.charg.controller;
import cn.zy.charg.dao.PileMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.zy.charg.bean.Msg;
import cn.zy.charg.bean.Pile;
import cn.zy.charg.service.PileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class PileController {
    @Autowired
    private PileService pileService;

    @RequestMapping("/getpiles")
    public List<Pile> getpiles(){
        List<Pile> piles = pileService.getAll();
        System.out.println("asas");
        return piles;
    }
}
