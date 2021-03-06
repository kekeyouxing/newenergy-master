package newenergy.db.service;

import newenergy.core.util.SpringUtil;
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
import newenergy.db.util.SortUtil;
import newenergy.db.util.StringUtilCorey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by HUST Corey on 2019-04-18.
 */
@Service
public class PlotFactorService implements Searchable<ApplyFactor, ApplyFactorPredicate> {
//    @Autowired
    private ApplyFactorRepository repository;
//    @Autowired
    private CorrPlotService corrPlotService;
//    @Autowired
    CorrPlotRepository corrPlotRepository;
//    @Autowired
    NewenergyAdminRepository newenergyAdminRepository;

    public PlotFactorService(){
        repository = SpringUtil.getBean(ApplyFactorRepository.class);
        corrPlotService = SpringUtil.getBean(CorrPlotService.class);
        corrPlotRepository = SpringUtil.getBean(CorrPlotRepository.class);
        newenergyAdminRepository = SpringUtil.getBean(NewenergyAdminRepository.class);
    }

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

    public Page<ApplyFactor> findAllByPlotNum(String plotNum, Pageable pageable, Sort sort){
        ApplyFactorPredicate predicate = new ApplyFactorPredicate();
        predicate.setPlots(Collections.singletonList(plotNum));
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

    public List<CorrPlot> findAllCorrPlotWithAlive(CorrPlotPredicate predicate){
        return corrPlotService.findAllCorrPlotWithAlive(predicate);
    }
    public Page<CorrPlot> findAllCorrPlotWithAlivePaged(CorrPlotPredicate predicate, Integer page, Integer limit){
        return corrPlotService.findAllCorrPlotWithAlivePaged(predicate,page,limit);
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
        List<CorrPlot> allRes = findAllCorrPlotWithAlive(predicate);
        CorrPlot res = allRes.isEmpty()?null:allRes.get(0);
        if(res == null) return ResultConstant.ERR;
        BigDecimal originFactor = res.getPlotFactor();
        applyFactor.setOriginFactor(originFactor);
        return repository.saveAndFlush(applyFactor)==null?ResultConstant.ERR:ResultConstant.OK;
    }

//    更新审核通过待生效的充值系数
    public void updateFactor(){
        List<ApplyFactor> list = repository.findAllByStateOrderByCheckTime(1);
        for (ApplyFactor applyFactor :
                list) {
            CorrPlot corrPlot = corrPlotService.findPlotByPlotNum(applyFactor.getPlotNum());
            corrPlot.setPlotFactor(applyFactor.getUpdateFactor());
            corrPlotService.updateCorrPlot(corrPlot,applyFactor.getLaborId());
            applyFactor.setState(3);
            repository.saveAndFlush(applyFactor);
        }

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
                    List<CorrPlot> allRes = findAllCorrPlotWithAlive(corrPlotPredicate);
                    CorrPlot res = allRes==null?null:allRes.get(0);
                    String plotNum = "";
                    if(res != null){
                        plotNum = res.getPlotNum();
                    }
                    lists.add(criteriaBuilder.equal(root.get("plotNum").as(String.class),plotNum));
                }
                if(predicate.getState() != null){
                    lists.add(criteriaBuilder.equal(root.get("state").as(Integer.class),predicate.getState()));
                }
                if(predicate.getPlots() != null){
                    Path<Object> path = root.get("plotNum");
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(path);
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
                    lists.add(criteriaBuilder.and(in));
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
