package newenergy.db.repository;

import newenergy.db.domain.RechargeRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RechargeRecordRepository extends JpaRepository<RechargeRecord,Integer>, JpaSpecificationExecutor<RechargeRecord> {

    RechargeRecord findFirstById(Integer id);

    RechargeRecord findFirstByOrderSn(String orderSn);

    List<RechargeRecord> findAll(Specification<RechargeRecord> specification);

}
