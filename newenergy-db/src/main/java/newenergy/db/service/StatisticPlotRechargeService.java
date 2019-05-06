package newenergy.db.service;

import newenergy.db.domain.StatisticPlotRecharge;
import newenergy.db.repository.StatisticPlotRechargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticPlotRechargeService {
    @Autowired
    private StatisticPlotRechargeRepository statisticPlotRechargeRepository;

    /**
     * 获取当期小区充值和消费月报表
     * @param curTime   当期时间
     * @param plotNum    小区名称（查找条件，可以空）
     * @param page
     * @param limit
     * @return
     */
    public Page<StatisticPlotRecharge> curPlotRecharge(LocalDate curTime, String plotNum, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Specification<StatisticPlotRecharge> specification = querySelection(plotNum, curTime);
        return statisticPlotRechargeRepository.findAll(specification, pageable);
    }

    /**
     * 生成小区充值及消费月报表
     * @param plotRecharge
     * @return
     */
    public StatisticPlotRecharge addPlotRecharge(StatisticPlotRecharge plotRecharge) {
        return statisticPlotRechargeRepository.save(plotRecharge);
    }

    /**
     * 多条件查找
     * @param plotNum
     * @return
     */
    public Specification<StatisticPlotRecharge> querySelection(String plotNum, LocalDate curTime) {
        Specification<StatisticPlotRecharge> specification = new Specification<StatisticPlotRecharge>() {
            @Override
            public Predicate toPredicate(Root<StatisticPlotRecharge> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(!StringUtils.isEmpty(plotNum)) {
                    predicates.add(criteriaBuilder.equal(root.get("plotNum"), plotNum));
                }
                if(!StringUtils.isEmpty(curTime)) {
                    predicates.add(criteriaBuilder.like(root.get("updateTime").as(String.class), curTime+"%"));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return specification;
    }


}
