package newenergy.db.repository;

import newenergy.db.domain.ApplyFactor;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by HUST Corey on 2019-04-18.
 */
public interface ApplyFactorRepository extends JpaRepository<ApplyFactor,Integer>, JpaSpecificationExecutor<ApplyFactor> {
    ApplyFactor findFirstById(Integer id);

    List<ApplyFactor> findAllByState(Integer state);
}
