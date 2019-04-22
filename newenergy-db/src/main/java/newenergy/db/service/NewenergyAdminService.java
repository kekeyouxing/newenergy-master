package newenergy.db.service;

import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.domain.NewenergyRole;
import newenergy.db.domain.Resident;
import newenergy.db.repository.NewenergyAdminRepository;
import newenergy.db.template.LogicOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewenergyAdminService extends LogicOperation<NewenergyAdmin> {
    @Autowired
    private NewenergyAdminRepository adminRepository;

    public List<NewenergyAdmin> findAdmin(String username) {

        Specification specification = getListSpecification(username);
        return adminRepository.findAll(specification);

    }
    public Page<NewenergyAdmin> querySelective(String username, Integer page, Integer size){

        Pageable pageable = PageRequest.of(page, size);
        //动态条件
        Specification specification = getListSpecification(username);

        return adminRepository.findAll(specification, pageable);

    }
    private Specification<NewenergyRole> getListSpecification(String username){

        //动态条件
        Specification<NewenergyRole> specification = new Specification<NewenergyRole>() {
            @Override
            public Predicate toPredicate(Root<NewenergyRole> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(username!=null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("username"), "%"+username+"%")));
                }
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("safeDelete"), 0)));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return specification;

    }

    public NewenergyAdmin add(NewenergyAdmin admin, Integer userid){

        return addRecord(admin, userid, adminRepository);

    }

    public NewenergyAdmin findById(Integer id) {

        return adminRepository.getOne(id);

    }

    public NewenergyAdmin updateById(NewenergyAdmin admin, Integer userId) {
        return updateRecord(admin, userId, adminRepository);
    }

    public void deleteById(Integer id, Integer userId) {

        deleteRecord(id, userId, adminRepository);

    }
}
