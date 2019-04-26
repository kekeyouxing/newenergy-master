package newenergy.db.service;

import newenergy.db.constant.SafeConstant;
import newenergy.db.domain.Resident;
import newenergy.db.repository.ResidentRepository;
import newenergy.db.template.LogicOperation;
import newenergy.db.util.StringUtilCorey;
import org.apache.tomcat.util.http.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResidentService extends LogicOperation<Resident> {
    @Autowired
    private ResidentRepository residentRepository;

    /**
    * @Param
    * @Param address_nums 装机地址模糊查询对应编号
    * @return 返回十页的resident数据
    */
    public Page<Resident> querySelective(String userName, List<String> addressNums, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification specification = getListSpecification(userName, addressNums);

        return residentRepository.findAll(specification, pageable);
    }

    /**查找同一地址同一房间的装机纪录
     * @Param  address_num  地址编号
     * @Param  room_num    房间号
     * @return
     */
    public List<Resident> queryDevice(String address_num, String room_num) {
        Resident resident = new Resident();
        resident.setAddressNum(address_num);
        resident.setRoomNum(room_num);
        Sort sort = new Sort(Sort.Direction.ASC, "deviceSeq");
        List<Resident> residents = residentRepository.findAll(findSearch(resident), sort);
        return residents;
    }

    //新增纪录
    public Resident addResident(Resident resident, Integer userid) {
        return addRecord(resident, userid, residentRepository);
    }

    /**
     * 验证户主名与登记号是否一致，默认查找的安全属性safeDelete为0
     * @param username 户主名
     * @param registerId 登记号
     * @return boolean
     */
    public boolean verifyUserNameAndRegisterId(String username, String registerId){
        Resident resident = residentRepository.findFirstByUserNameAndRegisterIdAndSafeDelete(username, registerId, 0);
        if(resident != null) {
            return true;
        }
        return false;
    }

    /**
     * 根据小区编号查找居民用户
     * @Param plot_num 小区编号
     * @return List<resident>
     */
    public List<Resident> findByPlotNum(String plotNum) {
        return residentRepository.findByPlotNumAndSafeDelete(plotNum,0);
    }

    /**
     * 通过设备号查询所在小区编号
     * @param register_id
     * @return String plotNum-小区编号
     */
    public String findPlotNumByRegisterid(String register_id,Integer safe_delete){
        return residentRepository.findFirstByRegisterIdAndSafeDelete(register_id,safe_delete).getPlotNum();
    }
    /**
     * 根据登记号查找居民用户
     * @param registerId  登记号
     * @return
     */
    public Resident fingByRegisterId(String registerId) {
        return residentRepository.findByRegisterIdAndSafeDelete(registerId, 0);
    }

    /**
     * 根据小区编号和登记号查找居民用户
     * @param plotNum  小区编号
     * @param registerId   登记号
     * @param page
     * @param limit
     * @return
     */
    public Page<Resident> findByPlotNumAndRegisterId(String plotNum, String registerId, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit);

        Resident resident = new Resident();
        resident.setPlotNum(plotNum);
        resident.setRegisterId(registerId);
        Specification specification = findSearch(resident);
        return residentRepository.findAll(specification, pageable);
    }

    public Integer findByPlotNumAndRegisterIdSize(String plotNum,String registerId){
        Resident resident = new Resident();
        resident.setPlotNum(plotNum);
        resident.setRegisterId(registerId);
        Specification specification = findSearch(resident);
        return residentRepository.findAll(specification).size();
    }

    /**
     * 修改居民用户表记录
     * @param resident
     * @param userid  操作人id
     * @return
     */
    public Resident updateResident(Resident resident, Integer userid) {
        return updateRecord(resident, userid, residentRepository);
    }

    /**
     * 删除居民用户记录
     * @param id    删除记录的id
     * @param userid   操作人id
     */
    public void deleteResident(Integer id, Integer userid) {
        deleteRecord(id, userid, residentRepository);
    }

    private Specification<Resident> getListSpecification(String userName, List<String> addressNums) {
        //动态添加搜索条件
        Specification<Resident> specification = new Specification<Resident>() {
            @Override
            public Predicate toPredicate(Root<Resident> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(!StringUtils.isEmpty(userName)){
                    predicates.add(criteriaBuilder.like(root.get("userName"), "%"+userName+"%"));
                }
                if(addressNums.size()!=0) {
                    Path<Object> path = root.get("addressNum");
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(path);
                    for(String address_num: addressNums) {
                        in.value(address_num);
                    }
                    predicates.add(criteriaBuilder.and(in));
                }
                predicates.add(criteriaBuilder.equal(root.get("safeDelete"), 0));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return specification;
    }

    private Specification<Resident> findSearch(Resident resident) {
        Specification<Resident> specification = new Specification<Resident>() {
            @Override
            public Predicate toPredicate(Root<Resident> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(!StringUtils.isEmpty(resident.getAddressNum())) {
                    predicates.add(criteriaBuilder.equal(root.get("addressNum"), resident.getAddressNum()));
                }
                if(!StringUtils.isEmpty(resident.getRoomNum())) {
                    predicates.add(criteriaBuilder.equal(root.get("roomNum"), resident.getRoomNum()));
                }
                if(!StringUtils.isEmpty(resident.getPlotNum())) {
                    predicates.add(criteriaBuilder.equal(root.get("plotNum"), resident.getPlotNum()));
                }
                if(!StringUtils.isEmpty(resident.getRegisterId())) {
                    predicates.add(criteriaBuilder.equal(root.get("registerId"), resident.getRegisterId()));
                }
                predicates.add(criteriaBuilder.equal(root.get("safeDelete"), 0));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return specification;
    }

    /**
     * by Zeng Hui
     * 用于故障记录的用户列表
     * @param resident
     * @return
     */
    public Specification<Resident> findByPlotNumOrSearch(Resident resident){
        return (root,cq,cb)-> {
            List<Predicate> predicates = new ArrayList<>();
            if(!StringUtilCorey.emptyCheck(resident.getPlotNum())) {
                predicates.add(cb.equal(root.get("plotNum"), resident.getPlotNum()));
            }
            if(!StringUtilCorey.emptyCheck(resident.getRegisterId())){
                predicates.add(cb.equal(root.get("registerId"), resident.getRegisterId()));
            }
            if(!StringUtilCorey.emptyCheck(resident.getUserName())){
                predicates.add(cb.equal(root.get("userName"), resident.getUserName()));
            }
            predicates.add(cb.equal(root.get("safeDelete"), SafeConstant.SAFE_ALIVE));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }



}
