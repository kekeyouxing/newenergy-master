package newenergy.db.template;

/**
 * Created by HUST Corey on 2019-04-16.
 */

import newenergy.db.predicate.PredicateExecutor;
import newenergy.db.predicate.PredicateFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @param <T> 实体类类型
 * @param <S> 搜索predicate类型
 */
public interface Searchable<T,S> {
    Specification<T> addConditioin(S predicate, Specification<T> other);
    default Page<T> findByPredicate(S predicate, Pageable pageable, Sort sort){
        Specification<T> cond = addConditioin(predicate,null);
        return PredicateExecutor.findBySpecification(getRepository(),cond,pageable,sort);
    }
    default Page<T> findByPredicateWithAive(S predicate, Pageable pageable, Sort sort){
        Specification<T> cond = addConditioin(predicate,PredicateFactory.getAliveSpecification());
        return PredicateExecutor.findBySpecification(getRepository(),cond,pageable,sort);
    }
    default T findOneByPredicate(S predicate, Pageable pageable, Sort sort) {
        Page<T> allRes = findByPredicate(predicate,pageable,sort);
        return allRes.get().findFirst().orElse(null);
    }
    default T findOneByPredicateWithAive(S predicate, Pageable pageable, Sort sort){
        Page<T> allRes = findByPredicateWithAive(predicate,pageable,sort);
        return allRes.get().findFirst().orElse(null);
    }

    JpaSpecificationExecutor<T> getRepository();
}
