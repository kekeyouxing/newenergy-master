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
import org.springframework.util.StringUtils;

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
    public Page<CorrAddress> querySelective(String addressDtl, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification specification = getListSpecification(addressDtl, null);
        return corrAddressRepository.findAll(specification, pageable);
    }

    /**
     * 根据小区编号查找地址
     * @param plotNum
     * @return
     */
    public List<CorrAddress> findByPlotNum(String plotNum) {
        List<CorrAddress> corrAddresses = corrAddressRepository.findAll(getListSpecification(null, plotNum));
        return corrAddresses;
    }

    /**
     * 根据小区地址查找相关地址编号
     * @param addressDtl
     * @return
     */
    public List<String> queryAddress(String addressDtl) {
        List<CorrAddress> corrAddresses = corrAddressRepository.findAll(getListSpecification(addressDtl, null));
        List<String> address_nums = new ArrayList<>();
        for(CorrAddress corrAddress:  corrAddresses) {
            address_nums.add(corrAddress.getAddressNum());
        }
        return address_nums;
    }

    public String findAddressDtlByAddressNum(String addressNum){
        return corrAddressRepository
                .findFirstByAddressNumAndSafeDelete(addressNum,0)
                .getAddressDtl();
    }

    //添加纪录
    public CorrAddress addCorrAddress(CorrAddress corrAddress, Integer userid) {
        return addRecord(corrAddress, userid, corrAddressRepository);
    }

    //修改记录
    public CorrAddress updateCorrAddress(CorrAddress corrAddress, Integer userid) {
        return updateRecord(corrAddress, userid, corrAddressRepository);
    }

    //删除记录
    public void deleteCorrAddress(Integer id, Integer userid) {
        deleteRecord(id, userid, corrAddressRepository);
    }


    //多条件动态查询
    private Specification<CorrAddress> getListSpecification(String addressDtl,String plotNum) {
        Specification<CorrAddress> specification = new Specification<CorrAddress>() {
            @Override
            public Predicate toPredicate(Root<CorrAddress> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(!StringUtils.isEmpty(addressDtl)) {
                    predicates.add(criteriaBuilder.like(root.get("addressPlot"), "%"+addressDtl+"%"));
                }
                if(!StringUtils.isEmpty(plotNum)){
                    predicates.add(criteriaBuilder.like(root.get("addressNum"), plotNum+"%"));
                }
                predicates.add(criteriaBuilder.equal(root.get("safeDelete"), 0));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return specification;
    }
}
