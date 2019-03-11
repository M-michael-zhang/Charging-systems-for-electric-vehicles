package cn.zy.charg.controller;
import cn.zy.charg.bean.Pile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.zy.charg.service.PileService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class PileController {
    @Autowired
    private PileService pileService;

    @RequestMapping("/getpiles")
    public List<Pile> getpiles(){
        List<Pile> piles = pileService.getAll();
        return piles;
    }

    @RequestMapping("/getPileById")
    public Pile getpiles(HttpServletRequest request){
        Integer id = Integer.valueOf(request.getParameter("id"));
        Pile piles = pileService.getPile(id);
        return piles;
    }

    @RequestMapping("/")
    public String addpile(HttpServletRequest request){
        String a = request.getParameter("X");
        String b = request.getParameter("Y");
        Pile p = new Pile(null,Double.valueOf(a), Double.valueOf(b),1 );
        pileService.savePile(p);
        return "1";
    }
}
