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
     */
//    List<NewenergyAdmin> findAllByRealNameAndDeleted(String realName, Boolean deleted);
//    NewenergyAdmin findFirstByIdAndDeleted(Integer id, Boolean deleted);

    /**
     * TODO
     * by Zeng Hui
     * 待修改
     */
    List<NewenergyAdmin> findAllByRealNameAndSafeDelete(String realName,Integer safeDelete);
    NewenergyAdmin findFirstByIdAndSafeDelete(Integer id, Integer safeDelete);

}
