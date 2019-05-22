package newenergy.db.service;

import newenergy.db.domain.Resident;
import newenergy.db.domain.StatisticConsume;
import newenergy.db.repository.StatisticConsumeRepository;
import newenergy.db.util.StringUtilCorey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticConsumeService {
    @Autowired
    private StatisticConsumeRepository statisticConsumeRepository;

    @Autowired
    private ResidentService residentService;

    /**
     * 根据登记号查找当期用水量月表
     * @param registerId   登记号
     * @param currentTime   当期时间
     * @return   每个月统计一次，只有一条记录
     */
    public StatisticConsume findByRegisterIdAndUpdateTime(String registerId, LocalDate currentTime) {
        Specification<StatisticConsume> specification = querySelection(registerId, currentTime,null);
        List<StatisticConsume> consumes = statisticConsumeRepository.findAll(specification, Sort.by(Sort.Direction.DESC,"updateTime"));
        if(!consumes.isEmpty()) return consumes.get(0);
        return null;
    }

    /**
     * 查找用户用水量月表中当期所有纪录
     * @param page
     * @param limit
     * @param curTime
     * @param plotNum  不为空时，按照小区查找
     * @return
     */
    public Page<StatisticConsume> getCurConsume(Integer page, Integer limit,LocalDate curTime, String plotNum) {
        Pageable pageable = PageRequest.of(page, limit);
        Specification<StatisticConsume> specification = querySelection(null, curTime, plotNum);
        return statisticConsumeRepository.findAll(specification, pageable);
    }

    public List<StatisticConsume> getCurConsume(LocalDate curTime, String plotNum){
        Specification<StatisticConsume> specification = querySelection(null, curTime, plotNum);
        return statisticConsumeRepository.findAll(specification);
    }



    /**
     * 每月定时生成设备用水量月表
     * @param consume
     * @return
     */
    public StatisticConsume addConsume(StatisticConsume consume) {
        return statisticConsumeRepository.save(consume);
    }

    /**
     * 多条件查找功能
     * @param registerId  登记号
     * @Param curTime 当期时间
     * @Param plotNum 小区编号
     * @return
     */
    public Specification<StatisticConsume> querySelection(String registerId, LocalDate curTime, String plotNum) {
        Specification<StatisticConsume> specification = new Specification<StatisticConsume>() {
            @Override
            public Predicate toPredicate(Root<StatisticConsume> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(!StringUtils.isEmpty(registerId)) {
                    predicates.add(criteriaBuilder.equal(root.get("registerId"), registerId));
                }
                if(!StringUtils.isEmpty(plotNum)) {
                    List<Resident> residents = residentService.findByPlotNum(plotNum);
                    Path<Object> path = root.get("registerId");
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(path);
                    for(Resident resident: residents) {
                        in.value(resident.getRegisterId());
                    }
                    predicates.add(criteriaBuilder.and(in));
                }
                if(!StringUtils.isEmpty(curTime)) {
                    predicates.add(criteriaBuilder.like(root.get("updateTime").as(String.class), curTime+"%"));
                }

//                if(!StringUtils.isEmpty(start)) {
//                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("curUsed"), start));
//                }
//                if(!StringUtils.isEmpty(end)) {
//                    predicates.add(criteriaBuilder.lessThan(root.get("curUsed"), end));
//                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return specification;
    }
}
