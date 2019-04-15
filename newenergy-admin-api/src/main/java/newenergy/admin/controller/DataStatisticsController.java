package newenergy.admin.controller;

import newenergy.db.domain.Resident;
import newenergy.db.service.ResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/data")
@Validated
public class DataStatisticsController {

    @Autowired
    ResidentService residentService;

    @GetMapping("/consume")
    public Object consume(@RequestParam String plotNum) {
        List<Resident> residents = residentService.findByPlotNum(plotNum);
        for(Resident resident: residents) {

        }
        return null;
    }

}
