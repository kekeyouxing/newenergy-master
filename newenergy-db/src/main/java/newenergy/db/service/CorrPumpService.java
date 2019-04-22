package newenergy.db.service;

import newenergy.db.domain.CorrPump;
import newenergy.db.repository.CorrPumpRepository;
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
public class CorrPumpService extends LogicOperation<CorrPump> {
    @Autowired
    private CorrPumpRepository corrPumpRepository;

    //获取所有未删除纪录
    public List<CorrPump> findAll() {
        return corrPumpRepository.findAllBySafeDelete(0);
    }


    //根据小区信息获取分页数据
    public Page<CorrPump> querySelective(String pumpDtl, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Specification specification = getListSpecification(pumpDtl, null);
        return corrPumpRepository.findAll(specification, pageable);
    }

    /**
     * 根据小区编号查找所在小区机房
     * @param plotNum
     * @return
     */
    public List<CorrPump> findByPlotNum(String plotNum) {
        return corrPumpRepository.findAll(getListSpecification(null, plotNum));
    }

    //新增纪录
    public CorrPump addCorrPump(CorrPump corrPump, Integer userid) {
        return addRecord(corrPump, userid, corrPumpRepository);
    }

    //修改记录
    public CorrPump updateCorrPump(CorrPump corrPump, Integer userid) {
        return updateRecord(corrPump, userid, corrPumpRepository);
    }

    //删除记录
    public void deleteCorrPump(Integer id, Integer userid) {
        deleteRecord(id, userid, corrPumpRepository);
    }

    private Specification<CorrPump> getListSpecification(String pumpDtl, String plotNum) {
        Specification<CorrPump> specification = new Specification<CorrPump>() {
            @Override
            public Predicate toPredicate(Root<CorrPump> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(!StringUtils.isEmpty(pumpDtl)) {
                    predicates.add(criteriaBuilder.like(root.get("pumpDtl"), "%"+pumpDtl+"%"));
                }
                if(!StringUtils.isEmpty(plotNum)) {
                    predicates.add(criteriaBuilder.like(root.get("pumpNum"), plotNum+"%"));
                }
                predicates.add(criteriaBuilder.equal(root.get("safeDelete"), 0));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return specification;
    }
}
