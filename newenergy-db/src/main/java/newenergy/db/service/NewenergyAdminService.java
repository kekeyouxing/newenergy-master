package newenergy.db.service;

import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.domain.NewenergyRole;
import newenergy.db.repository.NewenergyAdminRepository;
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
public class NewenergyAdminService {
    @Autowired
    private NewenergyAdminRepository adminRepository;

    public List<NewenergyAdmin> findAdmin(String username) {

        return adminRepository.getAllByUsernameAndDeletedIsFalse(username);

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
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("deleted"), false)));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return specification;

    }

    public void add(NewenergyAdmin admin){

        admin.setAddTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        admin.setDeleted(false);
        adminRepository.saveAndFlush(admin);

    }

    public NewenergyAdmin findById(Integer id) {

        return adminRepository.getOne(id);

    }

    public Object updateById(NewenergyAdmin admin) {
        return adminRepository.saveAndFlush(admin);
    }

    public void deleteById(Integer id) {
        NewenergyAdmin admin = adminRepository.getOne(id);
        admin.setDeleted(true);
        adminRepository.saveAndFlush(admin);
    }
}
