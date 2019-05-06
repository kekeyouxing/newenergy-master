package newenergy.db.predicate;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

/**
 * Created by HUST Corey on 2019-04-11.
 */
public class PredicateFactory {
    public  static <T> Specification<T> getAliveSpecification(){
        return (root,criteriaQuery,criteriaBuilder)->
        criteriaBuilder.and(criteriaBuilder.equal(root.get("safeDelete").as(Integer.class),0));
    }
    public  static <T> Specification<T> getAliveSpecification2(){
        return (root,criteriaQuery,criteriaBuilder)->
                criteriaBuilder.and(criteriaBuilder.equal(root.get("deleted").as(Boolean.class),false));
    }

}
