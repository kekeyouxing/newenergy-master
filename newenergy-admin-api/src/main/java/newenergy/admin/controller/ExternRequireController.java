package newenergy.admin.controller;

import newenergy.admin.background.service.DeviceRequireService;
import newenergy.db.domain.CorrPlot;
import newenergy.db.service.CorrPlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by HUST Corey on 2019-05-09.
 */
@RestController
public class ExternRequireController {
    @Autowired
    private CorrPlotService corrPlotService;
    @Autowired
    private DeviceRequireService deviceRequireService;

    @RequestMapping(value = "require/{plotDtl}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BigDecimal getRequire(@PathVariable String plotDtl){
        List<CorrPlot> corrPlotList =corrPlotService.findAllByPlotDtl(plotDtl);
        if(corrPlotList == null || corrPlotList.isEmpty()) return new BigDecimal(0);
        CorrPlot corrPlot = corrPlotList.get(0);
        return deviceRequireService.getRequire(corrPlot.getPlotNum());
    }
}
