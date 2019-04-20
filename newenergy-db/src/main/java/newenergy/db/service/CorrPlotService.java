package newenergy.db.service;

import newenergy.db.domain.CorrPlot;
import newenergy.db.predicate.CorrPlotPredicate;
import newenergy.db.predicate.PredicateFactory;
import newenergy.db.repository.CorrPlotRepository;
import newenergy.db.template.LogicOperation;
import newenergy.db.util.StringUtilCorey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CorrPlotService extends LogicOperation<CorrPlot> {
    @Autowired
    private CorrPlotRepository corrPlotRepository;

    public List<CorrPlot> findAll() {
        return corrPlotRepository.findAllBySafeDeleteOrderByPlotNum(0);
    }

    //根据小区地址搜索
    public Page<CorrPlot> querySelective(String plot_dlt, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification specification = getListSpecification(plot_dlt);
        return corrPlotRepository.findAll(specification, pageable);
    }

    //根据小区地址搜索小区编号
    public String findPlotNum(String plot_dlt) {
        return corrPlotRepository.findByPlotDtlAndSafeDelete(plot_dlt, 0).getPlotNum();
    }

    //新增数据
    public CorrPlot addCorrPlot(CorrPlot corrPlot, Integer userid) {
        return addRecord(corrPlot, userid, corrPlotRepository);
    }

    //修改数据
    public CorrPlot updateCorrPlot(CorrPlot corrPlot, Integer userid) {
        return updateRecord(corrPlot, userid, corrPlotRepository);
    }

    //删除数据
    public void deleteCorrPlot(Integer id, Integer userid) {
        deleteRecord(id, userid, corrPlotRepository);
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

    /**
     * 通过小区号获取充值系数
     * @param plot_num
     * @return Double plotFactor 充值系数
     */
    public BigDecimal findPlotFacByPlotNum(String plot_num,Integer safe_delete){
        return corrPlotRepository.findFirstByPlotNumAndSafeDelete(plot_num,safe_delete).getPlotFactor();
    }

    /**
     * by Zeng Hui
     * @param page start with 0
     * @param limit
     * @return
     */
    public Page<CorrPlot> findAllCorrPlotWithAlive(CorrPlotPredicate predicate, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "plotNum"));
        Specification<CorrPlot> specification = (Root<CorrPlot> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            List<Predicate> lists = new ArrayList<>();
            if (!StringUtilCorey.emptyCheck(predicate.getPlotDtl())) {
                lists.add(cb.like(root.get("plotDtl").as(String.class), StringUtilCorey.getMod(predicate.getPlotDtl())));
            }
            if (!StringUtilCorey.emptyCheck(predicate.getPlotNum())) {
                lists.add(cb.equal(root.get("plotNum").as(String.class), predicate.getPlotNum()));
            }
            Predicate[] arr = new Predicate[lists.size()];
            return cb.and(lists.toArray(arr));
        };
        specification = specification.and(PredicateFactory.getAliveSpecification());
        return corrPlotRepository.findAll(specification, pageable);
    }
}
