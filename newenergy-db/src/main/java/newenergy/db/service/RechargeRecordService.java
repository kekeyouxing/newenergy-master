package newenergy.db.service;

import newenergy.db.domain.RechargeRecord;
import newenergy.db.repository.CorrPlotRepository;
import newenergy.db.repository.RechargeRecordRepository;
import newenergy.db.repository.ResidentRepository;
import newenergy.db.template.LogicOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RechargeRecordService extends LogicOperation<RechargeRecord> {

    @Autowired
    private RechargeRecordRepository repository;

    /**
     * 通过商户订单号获取订单
     * @param orderSn
     * @return RechargeRecord
     */
    public RechargeRecord findBySn(String orderSn){
        return repository.findFirstByOrderSn(orderSn);
    }
    /**
     * 添加记录
     * @param require 不包括id
     * @param userid 操作者用户id
     * @return
     */
    public RechargeRecord addRechargeRecord(RechargeRecord require, Integer userid){
        return addRecord(require,userid,repository);
    }

    /**
     * 逻辑修改
     * @param require 包括id
     * @param userid 操作者用户id
     * @return
     */
    public RechargeRecord updateRechargeRecord(RechargeRecord require, Integer userid){
        return updateRecord(require,userid,repository);
    }

    /**
     * 逻辑删除
     * @param id 待删除记录id
     * @param userid 操作者用户id
     */
    public void deleteRechargeRecord(Integer id, Integer userid){
        deleteRecord(id,userid,repository);
    }

    public List<RechargeRecord> findByRegisterIdAndSafeDeleteAndState(String registerId, int safeDelete, int state){
//        safeDelete默认为0，registerid默认“”，state默认-1，为默认值时，插叙你所有类型，否则，查询指定类型
        if (registerId.equals("")&&(state==-1)){
            return repository.findAllBySafeDelete(safeDelete);
        }else if (registerId.equals("")){
            return repository.findAllBySafeDeleteAndState(safeDelete,state);
        }else if (state==-1){
            System.out.println(registerId);
            return repository.findAllByRegisterIdAndSafeDelete(registerId,safeDelete);
        }else {
            return repository.findAllByRegisterIdAndSafeDeleteAndState(registerId,safeDelete,state);
        }

    }

//    根据批量充值记录id,审核状态查询充值记录,当批量充值id为-1,审核状态为-1,则查询所有的批量充值记录,否则按条件查询
    public List<RechargeRecord> findByBatchRecordAndReviewState(Integer batchRecordId,Integer reviewState,Integer safeDelete){
        if ((batchRecordId==-1)&&(reviewState == -1)){
            return repository.findAllBySafeDelete(safeDelete);
        }else if (batchRecordId==-1){
            return repository.findAllByReviewStateAndSafeDelete(reviewState,safeDelete);
        }else if (reviewState==-1){
            return repository.findAllByBatchRecordIdAndSafeDelete(batchRecordId,safeDelete);
        }else {
            return repository.findAllByBatchRecordIdAndReviewStateAndSafeDelete(batchRecordId,reviewState,safeDelete);
        }
    }

//    根据id查询批量充值记录
    public RechargeRecord findById(int id){
        if (repository.findAllById(id).size()==0){
            return null;
        }else {
            return repository.findAllById(id).get(0);
        }
    }

    //TODO 这里生成一个唯一的商户订单号，但仍有两个订单相同的可能性
    public String generateOrderSn(){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        String now = df.format(LocalDate.now());
        int hashCodev = UUID.randomUUID().toString().hashCode();
        if (hashCodev < 0){
            hashCodev =- hashCodev;
        }
        return "pk"+now+String.format("%012d",hashCodev);
    }

    /**
     * 查找一定时间范围内的、登记号为registerId的充值记录
     * @param registerId 登记号
     * @param startDateTime 开始时间
     * @param endLocalDateTime 结束时间
     * @return 符合条件的RechargeRecord列表
     */
    public List<RechargeRecord> findByRegisterIdAndTime(String registerId, LocalDateTime startDateTime, LocalDateTime endLocalDateTime){
        Sort sort = new Sort(Sort.Direction.DESC, "recharge_time");
        return repository.findAll(registerId_timeInterval_spec(registerId, startDateTime, endLocalDateTime), sort);
    }

    /**
     * 在一定时间范围内的、登记号为registerId的充值记录的 specification
     * 安全属性safe_delete为0
     * @param registerId 登记号
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @return
     */
    private Specification<RechargeRecord> registerId_timeInterval_spec(String registerId, LocalDateTime startDateTime, LocalDateTime endDateTime){
        Specification<RechargeRecord> specification = new Specification<RechargeRecord>() {
            @Override
            public Predicate toPredicate(Root<RechargeRecord> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(null != registerId) {
                    predicates.add(criteriaBuilder.equal(root.get("register_id"), registerId));
                }
                if(null != startDateTime) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("recharge_time"), startDateTime));
                }
                if(null != endDateTime) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("recharge_time"), endDateTime));
                }
                predicates.add(criteriaBuilder.equal(root.get("safe_delete"), 0));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return specification;
    }
}
