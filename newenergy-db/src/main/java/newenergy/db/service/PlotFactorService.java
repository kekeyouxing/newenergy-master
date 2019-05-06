package newenergy.db.service;

import newenergy.db.constant.ApplyFactorConstant;
import newenergy.db.constant.ResultConstant;
import newenergy.db.constant.SafeConstant;
import newenergy.db.domain.ApplyFactor;
import newenergy.db.domain.CorrPlot;
import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.predicate.ApplyFactorPredicate;
import newenergy.db.predicate.CorrPlotPredicate;
import newenergy.db.repository.ApplyFactorRepository;
import newenergy.db.repository.CorrPlotRepository;
import newenergy.db.repository.NewenergyAdminRepository;
import newenergy.db.template.Searchable;
import newenergy.db.util.StringUtilCorey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HUST Corey on 2019-04-18.
 */
@Service
public class PlotFactorService implements Searchable<ApplyFactor, ApplyFactorPredicate> {
    @Autowired
    private ApplyFactorRepository repository;
    @Autowired
    private CorrPlotService corrPlotService;
    @Autowired
    CorrPlotRepository corrPlotRepository;
    @Autowired
    NewenergyAdminRepository newenergyAdminRepository;

    /**
     * 后台批量充值模块
     * 用于查询待审核的小区充值系数申请
     * @param pageable 分页（从0开始），可为null
     * @param sort 排序（默认按照id递增），可为null
     * @return 返回格式参考数据库apply_plot_factor
     */
    public Page<ApplyFactor> findAllUncheckApply(Pageable pageable, Sort sort){
        ApplyFactorPredicate predicate = new ApplyFactorPredicate();
        predicate.setState(ApplyFactorConstant.UNCHECK);
        return findByPredicate(predicate,pageable,sort);
    }

    /**
     * 后台批量充值模块
     * 用户对充值系数申请进行审核
     * @param userid 审核人的id
     * @param applyId 充值系数修改申请记录的id
     * @param state ApplyFactorConstant.ACCEPT 或 ApplyFactorConstant.REJECT
     * @return ResultConstant.OK 或 ResultConstant.ERR
     */
    public Integer updateApplyState(Integer userid, Integer applyId, Integer state){
        if(!state.equals(ApplyFactorConstant.ACCEPT) && !state.equals(ApplyFactorConstant.REJECT))
            return ResultConstant.ERR;
        ApplyFactor applyFactor = repository.findById(applyId).orElse(null);
        if(applyFactor == null) return ResultConstant.ERR;
        applyFactor.setCheckId(userid);
        applyFactor.setCheckTime(LocalDateTime.now());
        applyFactor.setState(state);
        return repository.saveAndFlush(applyFactor)==null?ResultConstant.ERR:ResultConstant.OK;
    }

    public Page<CorrPlot> findAllCorrPlotWithAlive(CorrPlotPredicate predicate, Integer page, Integer limit){
        return corrPlotService.findAllCorrPlotWithAlive(predicate,page,limit);
    }
    public String getPlotDtl(String plotNum){
        CorrPlot corrPlot = corrPlotRepository.findFirstByPlotNumAndSafeDelete(plotNum,SafeConstant.SAFE_ALIVE);
        return corrPlot==null?null:corrPlot.getPlotDtl();
    }
    public String getAdminName(Integer id){

        NewenergyAdmin admin = newenergyAdminRepository.findFirstByIdAndSafeDelete(id, SafeConstant.SAFE_ALIVE);

        return admin==null?null:admin.getRealName();
    }


    public Integer applyUpdateFactor(Integer id, String plotNum, BigDecimal updateFactor){
        ApplyFactor applyFactor = new ApplyFactor();
        applyFactor.setLaborId(id);
        applyFactor.setApplyTime(LocalDateTime.now());
        applyFactor.setPlotNum(plotNum);
        applyFactor.setUpdateFactor(updateFactor);
        applyFactor.setState(ApplyFactorConstant.UNCHECK);

        CorrPlotPredicate predicate = new CorrPlotPredicate();
        predicate.setPlotNum(plotNum);
        Page<CorrPlot> allRes = findAllCorrPlotWithAlive(predicate,0,1);
        CorrPlot res = allRes.get().findFirst().orElse(null);
        if(res == null) return ResultConstant.ERR;
        BigDecimal originFactor = res.getPlotFactor();
        applyFactor.setOriginFactor(originFactor);
        return repository.saveAndFlush(applyFactor)==null?ResultConstant.ERR:ResultConstant.OK;
    }
    @Override
    public Specification<ApplyFactor> addConditioin(ApplyFactorPredicate predicate, Specification<ApplyFactor> other) {
        Specification<ApplyFactor> specification = new Specification<ApplyFactor>() {
            @Override
            public Predicate toPredicate(Root<ApplyFactor> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> lists = new ArrayList<>();
                if(!StringUtilCorey.emptyCheck(predicate.getPlotDtl())){
                    CorrPlotPredicate corrPlotPredicate = new CorrPlotPredicate();
                    corrPlotPredicate.setPlotDtl(predicate.getPlotDtl());
                    Page<CorrPlot> allRes = findAllCorrPlotWithAlive(corrPlotPredicate,0,1);
                    CorrPlot res = allRes.get().findFirst().orElse(null);
                    String plotNum = "";
                    if(res != null){
                        plotNum = res.getPlotNum();
                    }
                    lists.add(criteriaBuilder.equal(root.get("plotNum").as(String.class),plotNum));
                }
                if(predicate.getState() != null){
                    lists.add(criteriaBuilder.equal(root.get("state").as(Integer.class),predicate.getState()));
                }

                Predicate[] arr = new Predicate[lists.size()];
                return criteriaBuilder.and(lists.toArray(arr));
            }
        };
        return specification;
    }

    @Override
    public JpaSpecificationExecutor<ApplyFactor> getRepository() {
        return repository;
    }

    public ApplyFactor findById(Integer id){
        return repository.findFirstById(id);
    }
}
