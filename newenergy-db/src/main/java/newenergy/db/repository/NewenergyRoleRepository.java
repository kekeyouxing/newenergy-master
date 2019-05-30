package newenergy.db.repository;

import newenergy.db.domain.NewenergyRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewenergyRoleRepository extends JpaRepository<NewenergyRole, Integer> ,JpaSpecificationExecutor<NewenergyRole> {

    public List<NewenergyRole> getAllByEnableIsTrueAndDeletedIsFalseAndIdIn(Integer[] roleIds);

    public List<NewenergyRole> getAllByEnableIsTrueAndDeletedIsFalse();

    public List<NewenergyRole> getAllByEnableIsTrueAndDeletedIsFalseAndNameEquals(String name);
}
