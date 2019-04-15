package newenergy.db.repository;

import newenergy.db.domain.BatchCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchCredentialRepository extends JpaRepository<BatchCredential,Integer>{

    List<BatchCredential> findAllByIdAndSafeDelete(Integer id,Integer safeDelete);
}
