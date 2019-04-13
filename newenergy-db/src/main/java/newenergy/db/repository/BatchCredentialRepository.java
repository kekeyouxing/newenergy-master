package newenergy.db.repository;

import newenergy.db.domain.BatchCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchCredentialRepository extends JpaRepository<BatchCredential,Integer>{
}
