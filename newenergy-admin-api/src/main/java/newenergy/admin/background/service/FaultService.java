package newenergy.admin.background.service;

import newenergy.core.pojo.MsgRet;
import newenergy.core.util.TimeUtil;
import newenergy.db.constant.FaultRecordConstant;
import newenergy.db.domain.*;
import newenergy.db.predicate.FaultRecordPredicate;
import newenergy.db.repository.ResidentRepository;
import newenergy.db.service.FaultRecordService;
import newenergy.db.service.ResidentService;
import newenergy.db.util.StringUtilCorey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HUST Corey on 2019-05-08.
 */
@Service
public class FaultService {
    @Autowired
    FaultRecordService faultRecordService;
    @Autowired
    ResidentService residentService;
    @Autowired
    ResidentRepository residentRepository;

    @Value("${server.port}")
    private String port;
    private String sendMsgPrefix = "http://localhost:";
    private String sendMsgSuffix = "/wx/fault/send";
    private RestTemplate restTemplate;

    public FaultService(){
        restTemplate = new RestTemplate();
    }

    public boolean isNewFault(String deviceNUm){
        Resident resident = residentService.findByDeviceNumWithAlive(deviceNUm);
        if(resident == null) return false;
        FaultRecordPredicate predicate = new FaultRecordPredicate();
        predicate.setRegisterId(resident.getRegisterId());
        predicate.setSolving(true);
        Page<FaultRecord>  records = faultRecordService.findByPredicate(predicate,null,null);
        return records.isEmpty();
    }

    public void addFault(String deviceNum, String faultDtl){
        FaultRecord faultRecord = new FaultRecord();
        Resident condition = new Resident();
        condition.setDeviceNum(deviceNum);
        List<Resident> allRes = residentRepository
                .findAll( residentService.findByPlotNumOrSearch(condition) );
        if(allRes==null||allRes.size() != 1) return;
        Resident resident = allRes.get(0);
        CorrPlotAdmin corrPlotAdmin = faultRecordService.getCorrPlotAdmin(resident.getPlotNum());
        CorrAddress corrAddress = faultRecordService.getCorrAddress(resident.getAddressNum());
        if(corrPlotAdmin == null || corrAddress == null) return;
        NewenergyAdmin servicer = faultRecordService.getNewenergyAdmin(corrPlotAdmin.getServicerId());
        faultRecord.setRegisterId(resident.getRegisterId());
        faultRecord.setMonitorId(corrPlotAdmin.getMonitorId());
        faultRecord.setServicerId(corrPlotAdmin.getServicerId());
        faultRecord.setFaultTime(TimeUtil.getUTCNow());
        faultRecord.setState(FaultRecordConstant.STATE_WAIT);
        faultRecord.setPhenomenon(faultDtl);
        FaultRecord result = faultRecordService.addRecord(faultRecord);
        if(result == null) return;
        if(servicer==null || StringUtilCorey.emptyCheck(servicer.getOpenid())) return;
        Map<String,Object> request = new HashMap<>();

        String partName = String.format("%s*",resident.getUserName().substring(0,1));
        request.put("partName",partName);
        request.put("faultId",result.getId());
        request.put("touser",servicer.getOpenid());
        request.put("phone",resident.getPhone());
        request.put("phenomenon",faultDtl);
        request.put("address",corrAddress.getAddressDtl());

        restTemplate.postForObject(sendMsgPrefix+port+sendMsgSuffix,request, MsgRet.class);
    }

}
