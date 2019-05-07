package newenergy.admin.controller;

import newenergy.admin.annotation.AdminLoginUser;
import newenergy.admin.util.IpUtil;
import newenergy.core.util.TimeUtil;
import newenergy.db.domain.ApplyFactor;
import newenergy.db.domain.CorrPlot;
import newenergy.db.domain.ManualRecord;
import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.predicate.ApplyFactorPredicate;
import newenergy.db.predicate.CorrPlotPredicate;
import newenergy.db.service.CorrPlotService;
import newenergy.db.service.ManualRecordService;
import newenergy.db.service.PlotFactorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HUST Corey on 2019-04-18.
 */

/**
 * TODO 修改充值系数不是立即生效，而是等到每月一号制成报表后生效
 */
@RestController
@RequestMapping("admin/factor")
public class PlotFactorController {
    @Autowired
    PlotFactorService plotFactorService;

    @Autowired
    CorrPlotService corrPlotService;

    @Autowired
    ManualRecordService manualRecordService;

    private static class SearchDTO{
        Integer id;
        Integer page;
        Integer limit;
        String plotDtl;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }

        public String getPlotDtl() {
            return plotDtl;
        }

        public void setPlotDtl(String plotDtl) {
            this.plotDtl = plotDtl;
        }
    }
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Map<String,Object> searchFactor(@RequestBody SearchDTO dto, @AdminLoginUser NewenergyAdmin user){
//        Integer id = dto.getId();
        Integer id = user.getId();
        Integer page = dto.getPage();
        Integer limit = dto.getLimit();
        String plotDtl = dto.getPlotDtl();

        Map<String,Object> ret = new HashMap<>();
        CorrPlotPredicate predicate = new CorrPlotPredicate();
        predicate.setPlotDtl(plotDtl);
        Page<CorrPlot> corrPlots = plotFactorService.findAllCorrPlotWithAlivePaged(predicate,page-1,limit);
        ret.put("total",corrPlots.getTotalElements());
        List<Map<String,Object>> list = new ArrayList<>();
        corrPlots.forEach(e->{
            Map<String,Object> item = new HashMap<>();
            item.put("plotDtl",e.getPlotDtl());
            item.put("plotNum",e.getPlotNum());
            item.put("plotFactor",e.getPlotFactor());
            list.add(item);
        });
        ret.put("list",list);
        return ret;
    }
    private static class UpdateDTO{
        Integer id;
        String plotNum;
        BigDecimal updateFactor;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getPlotNum() {
            return plotNum;
        }

        public void setPlotNum(String plotNum) {
            this.plotNum = plotNum;
        }

        public BigDecimal getUpdateFactor() {
            return updateFactor;
        }

        public void setUpdateFactor(BigDecimal updateFactor) {
            this.updateFactor = updateFactor;
        }
    }
    @RequestMapping(value = "update",method = RequestMethod.POST)
    public Integer updateFactor(@RequestBody UpdateDTO dto, @AdminLoginUser NewenergyAdmin user){
//        Integer id = dto.getId();
        Integer id = user.getId();
        String plotNum = dto.getPlotNum();
        BigDecimal updateFactor = dto.getUpdateFactor();

        return plotFactorService.applyUpdateFactor(id,plotNum,updateFactor);
    }
    private static class ApplySearchDTO{
        Integer id;
        String plotDtl;
        Integer page;
        Integer limit;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getPlotDtl() {
            return plotDtl;
        }

        public void setPlotDtl(String plotDtl) {
            this.plotDtl = plotDtl;
        }

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }
    }
    @RequestMapping(value = "apply/search",method = RequestMethod.POST)
    public Map<String,Object> searchApply(@RequestBody ApplySearchDTO dto, @AdminLoginUser NewenergyAdmin user){
//        Integer id = dto.getId();
        Integer id = user.getId();
        String plotDtl = dto.getPlotDtl();
        Integer page = dto.getPage();
        Integer limit = dto.getLimit();

        Map<String,Object> ret = new HashMap<>();
        ApplyFactorPredicate predicate = new ApplyFactorPredicate();
        predicate.setPlotDtl(plotDtl);
        Page<ApplyFactor> factors = plotFactorService.findByPredicate(predicate, PageRequest.of(page-1,limit), Sort.by(Sort.Direction.ASC,"plotNum"));
        ret.put("total",factors.getTotalElements());
        List<Map<String,Object>>  list = new ArrayList<>();
        factors.forEach(e->{
            Map<String,Object> item = new HashMap<>();
            item.put("id",e.getId());
            item.put("plotDtl",plotFactorService.getPlotDtl(e.getPlotNum()));
            item.put("originFactor",e.getOriginFactor());
            item.put("updateFactor",e.getUpdateFactor());
            item.put("monitorName",plotFactorService.getAdminName(e.getLaborId()));
            item.put("time", TimeUtil.getSeconds(e.getApplyTime()));
            item.put("state",e.getState());
            list.add(item);
        });
        ret.put("list",list);
        return ret;
    }

    @RequestMapping(value = "review",method = RequestMethod.POST)
    public Object reviewPlotFactor(@RequestBody PostInfo postInfo,
                                   HttpServletRequest request,
                                   @AdminLoginUser NewenergyAdmin user){
        ApplyFactor applyFactor = plotFactorService.findById(postInfo.getId());
        applyFactor.setState(postInfo.getReviewState());
//        if (postInfo.getReviewState()==1){
//            CorrPlot corrPlot = corrPlotService.findPlotByPlotNum(applyFactor.getPlotNum());
//            corrPlot.setPlotFactor(applyFactor.getUpdateFactor());
//            corrPlotService.updateCorrPlot(corrPlot,postInfo.getOperatorId());
//        }
        Integer state = plotFactorService.updateApplyState(user.getId(),postInfo.getId(),postInfo.getReviewState());
        manualRecordService.add(user.getId(), IpUtil.getIpAddr(request),5,postInfo.getId());
        Map<String,Object> result = new HashMap<>();
        result.put("state",state);
        return result;
    }

    private static class PostInfo{
        private Integer operatorId;
        private Integer id;
        private Integer reviewState;

        public Integer getOperatorId() {
            return operatorId;
        }

        public void setOperatorId(Integer operatorId) {
            this.operatorId = operatorId;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getReviewState() {
            return reviewState;
        }

        public void setReviewState(Integer reviewState) {
            this.reviewState = reviewState;
        }
    }
}
