package newenergy.db.repository;

import newenergy.db.domain.NewenergyAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewenergyAdminRepository extends JpaRepository<NewenergyAdmin, Integer> , JpaSpecificationExecutor<NewenergyAdmin> {


    /**
     * by Zeng Hui
     *
     */
    List<NewenergyAdmin> findAllByRealNameAndSafeDelete(String realName,Integer safeDelete);
    NewenergyAdmin findFirstByIdAndSafeDelete(Integer id, Integer safeDelete);
    NewenergyAdmin findFirstByOpenidAndSafeDelete(String openid, Integer safeDelete);
}
