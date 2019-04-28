package newenergy.db.service;

import newenergy.db.domain.CorrType;
import newenergy.db.repository.CorrTypeRepository;
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
public class CorrTypeService extends LogicOperation<CorrType> {
    @Autowired
    private CorrTypeRepository corrTypeRepository;

    //获取所有未删除数据
    public List<CorrType> findAll() {
        return corrTypeRepository.findAllBySafeDeleteOrderByTypeNum(0);
    }

    //根据机型信息获取纪录
    public Page<CorrType> querySelective(String typeDtl, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Specification<CorrType> specification = getListSpecification(typeDtl);
        return corrTypeRepository.findAll(specification, pageable);
    }

    /**
     * 根据机型编号查找
     * @param typeNum
     * @return
     */
    public CorrType findByTypeNum(String typeNum){
        return corrTypeRepository.findFirstByTypeNumAndSafeDelete(typeNum,0);
    }

    //新增纪录
    public CorrType addCorrType(CorrType corrType, Integer userid) {
        return addRecord(corrType, userid, corrTypeRepository);
    }

    //修改记录
    public CorrType updateCorrType(CorrType corrType, Integer userid) {
        return updateRecord(corrType, userid, corrTypeRepository);
    }

    //删除记录
    public void deleteCorrType(Integer id, Integer userid) {
        deleteRecord(id, userid, corrTypeRepository);
    }

    private Specification<CorrType> getListSpecification(String typeDtl) {
        Specification<CorrType> specification = new Specification<CorrType>() {
            @Override
            public Predicate toPredicate(Root<CorrType> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(!StringUtils.isEmpty(typeDtl)) {
                    predicates.add(criteriaBuilder.like(root.get("typeDtl"), "%"+typeDtl+"%"));
                }
                predicates.add(criteriaBuilder.equal(root.get("safeDelete"), 0));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return specification;
    }
}
