package cn.zy.charg.controller;
import cn.zy.charg.bean.Pile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.zy.charg.service.PileService;
import org.springframework.beans.factory.annotation.Autowired;

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
