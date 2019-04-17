package newenergy.db.predicate;

import newenergy.db.domain.FaultRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by HUST Corey on 2019-04-16.
 */
public class PredicateExecutor {
    /**
     * @param repository JpaSepcificationExecutor
     * @param specification 故障记录查询条件
     * @param pageable 分页（从0开始），可为null
     * @param sort 排序（默认按照id递增），可为null
     * @param <T> 实体类类型
     * @return
     */
    public static <T> Page<T> findBySpecification(JpaSpecificationExecutor<T> repository, Specification<T> specification, Pageable pageable, Sort sort){
        Sort newSort = null;
        Pageable newPageable = null;
        if(pageable == null){
            newSort = sort==null?Sort.by(Sort.Direction.ASC, "id"):sort;
            newPageable = PageRequest.of(0,(int)(repository).count(specification),newSort);
        }else{
            if(sort != null){
                newPageable = PageRequest.of(pageable.getPageNumber(),pageable.getPageSize(),sort);
            }else{
                newPageable = pageable;
            }
        }
        return repository.findAll(specification,newPageable);
    }



}
