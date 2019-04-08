package newenergy.db.service;

import newenergy.db.domain.CorrPlot;
import newenergy.db.repository.CorrPlotRepository;
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
public class CorrPlotService extends LogicOperation<CorrPlot> {
    @Autowired
    private CorrPlotRepository corrPlotRepository;

    public List<CorrPlot> findAll() {
        return corrPlotRepository.findAllBySafeDelete(0);
    }

    //根据小区地址搜索
    public Page<CorrPlot> querySelective(String plot_dlt, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification specification = getListSpecification(plot_dlt);
        return corrPlotRepository.findAll(specification, pageable);
    }

    //根据小区地址搜索小区编号
    public String fingPlotNum(String plot_dlt) {
        return corrPlotRepository.findByPlotDtlAndSafeDelete(plot_dlt, 0).getPlotNum();
    }

    //新增数据
    public CorrPlot addCorrPlot(CorrPlot corrPlot, Integer userid) {
        return addRecord(corrPlot, userid, corrPlotRepository);
    }

    private Specification<CorrPlot> getListSpecification(String plot_dlt) {
        Specification<CorrPlot> specification = new Specification<CorrPlot>() {
            @Override
            public Predicate toPredicate(Root<CorrPlot> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(plot_dlt!=null) {
                    predicates.add(criteriaBuilder.like(root.get("plot_dlt"), "%"+plot_dlt+"%"));
                }
                predicates.add(criteriaBuilder.equal(root.get("safe_delete"), 0));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return specification;
    }
}
