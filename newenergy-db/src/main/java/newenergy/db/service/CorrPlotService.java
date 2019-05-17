package newenergy.db.service;

import newenergy.db.domain.CorrPlot;
import newenergy.db.domain.Resident;
import newenergy.db.predicate.CorrPlotPredicate;
import newenergy.db.predicate.PredicateFactory;
import newenergy.db.repository.CorrPlotRepository;
import newenergy.db.repository.ResidentRepository;
import newenergy.db.template.LogicOperation;
import newenergy.db.util.StringUtilCorey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CorrPlotService extends LogicOperation<CorrPlot> {
    @Autowired
    private CorrPlotRepository corrPlotRepository;

    @Autowired
    private ResidentRepository residentRepository;

    /**
     * 查询所有小区，按照小区编号排序
     * @return
     */
    public List<CorrPlot> findAll() {
        return corrPlotRepository.findAllBySafeDeleteOrderByPlotNum(0);
    }

    /**
     * 查询所有小区，按照小区名称排序
     * @return
     */
    public List<CorrPlot> findAllOrderByPlot() {
        return corrPlotRepository.findAllBySafeDeleteOrderByPlotDtl(0);
    }

    //根据小区地址搜索
    public Page<CorrPlot> querySelective(String plot_dlt, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification specification = getListSpecification(plot_dlt);
        return corrPlotRepository.findAll(specification, pageable);
    }

    /**
     * 根据小区编号获取地址
     * @param plotNum
     * @return
     */
    public String findByPlotNum(String plotNum) {
        return corrPlotRepository.findFirstByPlotNumAndSafeDelete(plotNum, 0).getPlotDtl();
    }

    public CorrPlot findPlotByPlotNum(String plotNum){
        return corrPlotRepository.findFirstByPlotNumAndSafeDelete(plotNum,0);
    }

    /**
     * 根据id查找纪录
     * @param id
     * @return
     */
    public CorrPlot findById(Integer id){
        return corrPlotRepository.findById(id).get();
    }

    //根据小区地址搜索小区编号
    public String findPlotNum(String plotDtl) {
        return corrPlotRepository.findByPlotDtlAndSafeDelete(plotDtl, 0).getPlotNum();
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

    private Specification<CorrPlot> getListSpecification(String plotDtl) {
        Specification<CorrPlot> specification = new Specification<CorrPlot>() {
            @Override
            public Predicate toPredicate(Root<CorrPlot> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(!StringUtils.isEmpty(plotDtl)) {
                    predicates.add(criteriaBuilder.like(root.get("plotDtl"), "%"+plotDtl+"%"));
                }
                predicates.add(criteriaBuilder.equal(root.get("safeDelete"), 0));
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
    public BigDecimal findPlotFacByPlotNum(String plot_num){
        return corrPlotRepository.findFirstByPlotNumAndSafeDelete(plot_num, 0).getPlotFactor();
    }

    /**
     * by Zeng Hui
     * @return
     */
    public List<CorrPlot> findAllCorrPlotWithAlive(CorrPlotPredicate predicate) {
        Specification<CorrPlot> specification = (Root<CorrPlot> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            List<Predicate> lists = new ArrayList<>();
            if (!StringUtilCorey.emptyCheck(predicate.getPlotDtl())) {
                lists.add(cb.like(root.get("plotDtl").as(String.class), StringUtilCorey.getMod(predicate.getPlotDtl())));
            }
            if (!StringUtilCorey.emptyCheck(predicate.getPlotNum())) {
                lists.add(cb.equal(root.get("plotNum").as(String.class), predicate.getPlotNum()));
            }

            if(predicate.getPlots() != null){
                Path<Object> path = root.get("plotNum");
                CriteriaBuilder.In<Object> in = cb.in(path);
                if(predicate.getPlots().isEmpty()){
                    predicate.setPlots(corrPlotRepository
                            .findAll()
                            .stream()
                            .map(CorrPlot::getPlotNum)
                            .collect(Collectors.toList()));
                }
                for(String plot : predicate.getPlots()){
                    if(StringUtilCorey.emptyCheck(plot)) continue;
                    in.value(plot);
                }
                in.value("");
                lists.add(cb.and(in));
            }

            Predicate[] arr = new Predicate[lists.size()];
            return cb.and(lists.toArray(arr));
        };
        specification = specification.and(PredicateFactory.getAliveSpecification());
        return corrPlotRepository.findAll(specification);
    }

    public Page<CorrPlot> findAllCorrPlotWithAlivePaged(CorrPlotPredicate predicate, Integer page, Integer limit){
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "plotNum"));
        Specification<CorrPlot> specification = (Root<CorrPlot> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            List<Predicate> lists = new ArrayList<>();
            if (!StringUtilCorey.emptyCheck(predicate.getPlotDtl())) {
                lists.add(cb.like(root.get("plotDtl").as(String.class), StringUtilCorey.getMod(predicate.getPlotDtl())));
            }
            if (!StringUtilCorey.emptyCheck(predicate.getPlotNum())) {
                lists.add(cb.equal(root.get("plotNum").as(String.class), predicate.getPlotNum()));
            }

            if(predicate.getPlots() != null){
                Path<Object> path = root.get("plotNum");
                CriteriaBuilder.In<Object> in = cb.in(path);
                if(predicate.getPlots().isEmpty()){
                    predicate.setPlots(corrPlotRepository
                            .findAll()
                            .stream()
                            .map(CorrPlot::getPlotNum)
                            .collect(Collectors.toList()));
                }
                for(String plot : predicate.getPlots()){
                    if(StringUtilCorey.emptyCheck(plot)) continue;
                    in.value(plot);
                }
                in.value("");
                lists.add(cb.and(in));
            }

            Predicate[] arr = new Predicate[lists.size()];
            return cb.and(lists.toArray(arr));
        };
        specification = specification.and(PredicateFactory.getAliveSpecification());
        return corrPlotRepository.findAll(specification,pageable);
    }

    /**
     * by Zeng Hui
     * @param plotDtl
     * @return
     */
    public List<CorrPlot> findAllByPlotDtl(String plotDtl){
        Specification<CorrPlot> spec = getListSpecification(plotDtl);
        return corrPlotRepository.findAll(spec);
    }
}
