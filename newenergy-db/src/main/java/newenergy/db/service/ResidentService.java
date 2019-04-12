package newenergy.db.service;

import newenergy.db.domain.Resident;
import newenergy.db.repository.ResidentRepository;
import newenergy.db.template.LogicOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResidentService extends LogicOperation<Resident> {
    @Autowired
    private ResidentRepository residentRepository;

    //根据搜索条件查找
    /*
    * @Param
    * @Param address_nums 装机地址模糊查询对应编号
    * @return 返回十页的resident数据
    */
    public Page<Resident> querySelective(String user_name, List<String> address_nums, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification specification = getListSpecification(user_name, address_nums);

        return residentRepository.findAll(specification, pageable);
    }

    /*查找同一地址同一房间的装机纪录
     * @Param  address_num  地址编号
     * @Param  room_num    房间号
     * @return
     */
    public List<Resident> queryDevice(String address_num, String room_num) {
        Resident resident = new Resident();
        resident.setAddressNum(address_num);
        resident.setRoomNum(room_num);
        List<Resident> residents = residentRepository.findAll(findSearch(resident));
        return residents;
    }

    //新增纪录
    public Resident addResident(Resident resident, Integer userid) {
        return addRecord(resident, userid, residentRepository);
    }

    private Specification<Resident> getListSpecification(String user_name, List<String> address_nums) {
        //动态添加搜索条件
        Specification<Resident> specification = new Specification<Resident>() {
            @Override
            public Predicate toPredicate(Root<Resident> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(user_name!=null){
                    predicates.add(criteriaBuilder.like(root.get("user_name"), "%"+user_name+"%"));
                }
                if(address_nums.size()!=0) {
                    Path<Object> path = root.get("address_num");
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(path);
                    for(String address_num: address_nums) {
                        in.value(address_num);
                    }
                    predicates.add(criteriaBuilder.and(in));
                }
                predicates.add(criteriaBuilder.equal(root.get("safe_delete"), 0));
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
                if(resident.getAddressNum()!=null) {
                    predicates.add(criteriaBuilder.equal(root.get("address_num"), resident.getAddressNum()));
                }
                if(resident.getRoomNum()!=null) {
                    predicates.add(criteriaBuilder.equal(root.get("room_num"), resident.getRoomNum()));
                }
                predicates.add(criteriaBuilder.equal(root.get("safe_delete"), 0));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return specification;
    }

}
