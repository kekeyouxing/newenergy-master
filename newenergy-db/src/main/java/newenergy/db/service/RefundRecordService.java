package newenergy.db.service;

import newenergy.db.domain.RefundRecord;
import newenergy.db.repository.RefundRecordRepository;
import newenergy.db.template.LogicOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class RefundRecordService extends LogicOperation<RefundRecord>{

    @Autowired
    private RefundRecordRepository repository;

    /**
     * 添加记录
     * @param require 不包括id
     * @param userid 操作者用户id
     * @return
     */
    public RefundRecord addRefundRecord(RefundRecord require, Integer userid){
        return addRecord(require,userid,repository);
    }

    /**
     * 逻辑修改
     * @param require 包括id
     * @param userid 操作者用户id
     * @return
     */
    public RefundRecord updateRefundRecord(RefundRecord require, Integer userid){
        return updateRecord(require,userid,repository);
    }

    /**
     * 逻辑删除
     * @param id 待删除记录id
     * @param userid 操作者用户id
     */
    public void deleteRefundRecord(Integer id, Integer userid){
        deleteRecord(id,userid,repository);
    }


    /**
     * 按照id查找
     * @param id 待查id
     */
    public RefundRecord findById(int id){
        return repository.findFirstById(id);
    }


    /**
     * 根据注册id和状态查询退款记录，若为空则忽略该条件
     * @param registerId
     * @param state
     * @return
     */
    public List<RefundRecord> findByCondition(String registerId,Integer state){
        return repository.findAll(findAllByConditions(registerId,state));

    }

    /**
     * 根据注册id和状态查询退款记录，若为空则忽略该条件
     * @param registerId
     * @param state
     * @return
     */
    private Specification<RefundRecord> findAllByConditions(String registerId,Integer state){
        Specification<RefundRecord> specification = new Specification<RefundRecord>() {
            @Override
            public Predicate toPredicate(Root<RefundRecord> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (registerId != null){
                    predicates.add(criteriaBuilder.equal(root.get("registerId"),registerId));
                }
                if (state != null){
                    predicates.add(criteriaBuilder.equal(root.get("state"),state));
                }
                predicates.add(criteriaBuilder.equal(root.get("safeDelete"),0));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return specification;
    }

}
