package newenergy.db.service;

import newenergy.db.domain.BatchRecord;
import newenergy.db.repository.BatchRecordRepository;
import newenergy.db.template.LogicOperation;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class BatchRecordService extends LogicOperation<BatchRecord> {

    @Autowired
    private BatchRecordRepository repository;

    /**
     * 添加记录
     * @param require 不包括id
     * @param userid 操作者用户id
     * @return
     */
    public BatchRecord addBatchRecord(BatchRecord require, Integer userid){
        return addRecord(require,userid,repository);
    }

    /**
     * 逻辑修改
     * @param require 包括id
     * @param userid 操作者用户id
     * @return
     */
    public BatchRecord updateBatchRecord(BatchRecord require, Integer userid){
        return updateRecord(require,userid,repository);
    }

    /**
     * 逻辑删除
     * @param id 待删除记录id
     * @param userid 操作者用户id
     */
    public void deleteBatchRecord(Integer id, Integer userid){
        deleteRecord(id,userid,repository);
    }


//    根据id查询批量充值记录
    public BatchRecord queryById(int id){
        return repository.findFirstById(id);
    }

//    根据小区编号和审核状态查询批量充值记录，分页
    public Page<BatchRecord> findByConditions(String plotNum, Integer state, Integer page, Integer size){
        Sort sort = Sort.by(Sort.Direction.DESC,"safeChangedTime");
        Pageable pageable = PageRequest.of(page, size, sort);
        return repository.findAll(findAllByConditions(plotNum,state),pageable);
    }

//根据小区编号和审核状态查询批量充值记录
    public List<BatchRecord> findByConditions(String plotNum,Integer state){
        return repository.findAll(findAllByConditions(plotNum,state));
    }


    /**
     * 根据小区编号、状态查询批量充值
     * @param plotNum
     * @param state
     * @return
     */
    private Specification<BatchRecord> findAllByConditions(String plotNum,Integer state){
        Specification<BatchRecord> specification = new Specification<BatchRecord>() {
            @Override
            public Predicate toPredicate(Root<BatchRecord> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (plotNum!=null){
                    predicates.add(criteriaBuilder.equal(root.get("plotNum"),plotNum));
                }
                if (state != null){
                    if (state!=0){
                        predicates.add(criteriaBuilder.notEqual(root.get("state"),0));
                    }else {
                        predicates.add(criteriaBuilder.equal(root.get("state"),0));
                    }
                }
                predicates.add(criteriaBuilder.equal(root.get("safeDelete"),0));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return  specification;
    }


}
