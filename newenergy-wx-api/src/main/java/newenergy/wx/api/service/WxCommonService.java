package newenergy.wx.api.service;

import newenergy.core.util.JacksonUtil;
import newenergy.core.util.ResponseUtil;
import newenergy.db.service.ResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WxCommonService {
    @Autowired
    private ResidentService residentService;

    public Object query(String body){
        String registerId = JacksonUtil.parseString(body,"registerId");
        String userName = residentService.fingByRegisterId(registerId).getUserName();
        return ResponseUtil.ok(userName);
    }
}
