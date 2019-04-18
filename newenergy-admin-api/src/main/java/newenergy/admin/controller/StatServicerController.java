package newenergy.admin.controller;

import newenergy.db.domain.FaultRecord;
import newenergy.db.predicate.FaultRecordPredicate;
import newenergy.db.service.FaultRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Created by HUST Corey on 2019-04-18.
 */
@RestController
@RequestMapping("admin/stat")
public class StatServicerController {
    @Autowired
    private FaultRecordService faultRecordService;
    @RequestMapping(value = "list",method = RequestMethod.POST)
    public Map<String,Object> listServicers(Integer id, Integer year, Integer month, Integer page, Integer limit, String username){
        FaultRecordPredicate predicate = new FaultRecordPredicate();
        predicate.setFinishTime(LocalDateTime.of(2019,4,1,0,0));
        System.out.println(predicate.getFinishTime());
        Page<FaultRecord> allRes = faultRecordService.findByPredicate(predicate,null,null);
        allRes.forEach(e->{
            System.out.println(e.getId());
        });
        return null;
    }
}
