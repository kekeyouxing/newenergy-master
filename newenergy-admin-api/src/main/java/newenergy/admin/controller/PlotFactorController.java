package newenergy.admin.controller;

import newenergy.db.domain.ApplyFactor;
import newenergy.db.domain.CorrPlot;
import newenergy.db.predicate.ApplyFactorPredicate;
import newenergy.db.predicate.CorrPlotPredicate;
import newenergy.db.service.CorrPlotService;
import newenergy.db.service.PlotFactorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HUST Corey on 2019-04-18.
 */
@RestController
@RequestMapping("admin/factor")
public class PlotFactorController {
    @Autowired
    PlotFactorService plotFactorService;

    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Map<String,Object> searchFactor(Integer id, Integer page,Integer limit,String plotDtl){
        Map<String,Object> ret = new HashMap<>();
        CorrPlotPredicate predicate = new CorrPlotPredicate();
        predicate.setPlotDtl(plotDtl);
        Page<CorrPlot> corrPlots = plotFactorService.findAllCorrPlotWithAlive(predicate,page-1,limit);
        ret.put("total",corrPlots.getTotalElements());
        List<Map<String,Object>> list = new ArrayList<>();
        corrPlots.forEach(e->{
            Map<String,Object> item = new HashMap<>();
            item.put("plotDtl",e.getPlotDtl());
            item.put("plotNum",e.getPlotNum());
            item.put("plotFactor",e.getPlotFactor());
            list.add(item);
        });
        ret.put("list",list);
        return ret;
    }
    @RequestMapping(value = "update",method = RequestMethod.POST)
    public Integer updateFactor(Integer id, String plotNum, BigDecimal updateFactor){
        return plotFactorService.applyUpdateFactor(id,plotNum,updateFactor);
    }
    @RequestMapping(value = "apply/search",method = RequestMethod.POST)
    public Map<String,Object> searchApply(Integer id, String plotDtl, Integer page, Integer limit){
        Map<String,Object> ret = new HashMap<>();
        ApplyFactorPredicate predicate = new ApplyFactorPredicate();
        predicate.setPlotDtl(plotDtl);
        Page<ApplyFactor> factors = plotFactorService.findByPredicate(predicate, PageRequest.of(page-1,limit), Sort.by(Sort.Direction.ASC,"plotNum"));
        ret.put("total",factors.getTotalElements());
        List<Map<String,Object>>  list = new ArrayList<>();
        factors.forEach(e->{
            Map<String,Object> item = new HashMap<>();
            item.put("plotDtl",plotFactorService.getPlotDtl(e.getPlotNum()));
            item.put("originFactor",e.getOriginFactor());
            item.put("updateFactor",e.getUpdateFactor());
            item.put("monitorName",plotFactorService.getAdminName(e.getLaborId()));
            item.put("time",e.getApplyTime());
            item.put("state",e.getState());
            list.add(item);
        });
        ret.put("list",list);
        return ret;
    }
}
