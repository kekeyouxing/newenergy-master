package newenergy.db.service;

import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.domain.NewenergyRole;
import newenergy.db.domain.Resident;
import newenergy.db.predicate.AdminPredicate;
import newenergy.db.predicate.PredicateFactory;
import newenergy.db.repository.NewenergyAdminRepository;
import newenergy.db.template.LogicOperation;
import newenergy.db.util.StringUtilCorey;
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

    /**
     * by Zeng Hui
     * @param predicate 条件
     * @param pageable 分页
     * @param sort 排序
     * @return
     */
    public Page<NewenergyAdmin> findByPredicateWithAlive(AdminPredicate predicate,Pageable pageable,Sort sort) {
        Specification<NewenergyAdmin> specification = new Specification<NewenergyAdmin>() {
            @Override
            public Predicate toPredicate(Root<NewenergyAdmin> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<>();
                if (!StringUtilCorey.emptyCheck(predicate.getRealName())) {
                    list.add(cb.like(root.get("realName").as(String.class), StringUtilCorey.getMod(predicate.getRealName())));
                }
                if (predicate.getIds() != null) {
                    List<Predicate> subList = new ArrayList<>();
                    for (Integer id : predicate.getIds()) {
                        subList.add(cb.equal(root.get("id").as(Integer.class), id));
                    }
                    Predicate[] subArr = new Predicate[subList.size()];
                    list.add(cb.or(subList.toArray(subArr)));
                }
                Predicate[] arr = new Predicate[list.size()];
                arr = list.toArray(arr);
                return cb.and(list.toArray(arr));
            }
        };
        specification = specification.and(PredicateFactory.getAliveSpecification());
        if(pageable != null){
            if(sort != null){
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
            }else{
                pageable = PageRequest.of(pageable.getPageNumber(),pageable.getPageSize());
            }
        }else{
            pageable = Pageable.unpaged();
        }
        return adminRepository.findAll(specification,pageable);
    }

}
