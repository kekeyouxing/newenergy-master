package newenergy.db.service;

import newenergy.db.domain.CorrAddress;
import newenergy.db.repository.CorrAddressRepository;
import newenergy.db.template.LogicOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class CorrAddressService extends LogicOperation<CorrAddress> {
    @Autowired
    private CorrAddressRepository corrAddressRepository;

    //查找所有存在的纪录
    public List<CorrAddress> findAll() {
        return corrAddressRepository.findBySafeDelete(0);
    }

    //根据小区地址分页查找
    public Page<CorrAddress> querySelective(String address_dlt, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification specification = getListSpecification(address_dlt);
        return corrAddressRepository.findAll(specification, pageable);
    }

    //根据小区地址查找相关地址编号
    public List<String> queryAddress(String address_dlt) {
        List<CorrAddress> corrAddresses = corrAddressRepository.findAll(getListSpecification(address_dlt));
        List<String> address_nums = new ArrayList<>();
        for(CorrAddress corrAddress:  corrAddresses) {
            address_nums.add(corrAddress.getAddressNum());
        }
        return address_nums;
    }

    //添加纪录
    public CorrAddress addCorrAddress(CorrAddress corrAddress, Integer userid) {
        return addRecord(corrAddress, userid, corrAddressRepository);
    }


    //多条件动态查询
    private Specification<CorrAddress> getListSpecification(String address_dlt) {
        Specification<CorrAddress> specification = new Specification<CorrAddress>() {
            @Override
            public Predicate toPredicate(Root<CorrAddress> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(address_dlt!=null) {
                    predicates.add(criteriaBuilder.like(root.get("address_plot"), "%"+address_dlt+"%"));
                }
                predicates.add(criteriaBuilder.equal(root.get("safe_delete"), 0));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return specification;
    }
}
