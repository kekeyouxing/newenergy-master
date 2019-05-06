package newenergy.wx.api.service;

import newenergy.db.constant.SafeConstant;
import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.domain.Resident;
import newenergy.db.repository.NewenergyAdminRepository;
import newenergy.db.repository.ResidentRepository;
import newenergy.db.service.NewenergyAdminService;
import newenergy.db.service.ResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by HUST Corey on 2019-04-28.
 */
@Service
public class BindService {
    @Autowired
    private ResidentRepository residentRepository;
    @Autowired
    private ResidentService residentService;
    @Autowired
    private NewenergyAdminRepository newenergyAdminRepository;
    @Autowired
    private NewenergyAdminService newenergyAdminService;

    public List<Resident> getResidents(String openid){
        return residentRepository.findAllByOpenidAndSafeDelete(openid, SafeConstant.SAFE_ALIVE);
    }
    public Resident getResident(String registerId){
        return residentService.fingByRegisterId(registerId);
    }
    public Resident updateResident(Resident resident, Integer userid){
        return residentService.updateResident(resident,userid);
    }
    public NewenergyAdmin getAdmin(String username, String password){
        return newenergyAdminRepository.findFirstByUsernameAndPasswordAndSafeDelete(username,password,SafeConstant.SAFE_ALIVE);
    }
    public NewenergyAdmin getAdminByOpenid(String openid){
        return newenergyAdminRepository.findFirstByOpenidAndSafeDelete(openid,SafeConstant.SAFE_ALIVE);
    }
    public NewenergyAdmin updateAdmin(NewenergyAdmin admin,Integer userid){
        return newenergyAdminService.updateById(admin,userid);
    }
}
