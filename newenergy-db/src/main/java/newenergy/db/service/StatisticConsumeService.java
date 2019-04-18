package newenergy.db.service;

import newenergy.db.domain.StatisticConsume;
import newenergy.db.repository.StatisticConsumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class StatisticConsumeService {
    @Autowired
    private StatisticConsumeRepository statisticConsumeRepository;

    /**
     * 根据登记号查找当期用水量月表
     * @param registerId   登记号
     * @param currentTime   当期时间
     * @return   每个月统计一次，只有一条记录
     */
    public StatisticConsume findByRegisterIdAndUpdateTime(String registerId, LocalDateTime currentTime) {
        StatisticConsume statisticConsume = new StatisticConsume();
        statisticConsume.setRegisterId(registerId);
        statisticConsume.setUpdateTime(currentTime);
        Specification<StatisticConsume> specification = querySelection(statisticConsume);
        List<StatisticConsume> consumes = statisticConsumeRepository.findAll(specification);
        if(consumes.size()==1) {
            return consumes.get(0);
        }
        return null;
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
     * @param statisticConsume  赋值了查找条件的对象
     * @return
     */
    public Specification<StatisticConsume> querySelection(StatisticConsume statisticConsume) {
        Specification<StatisticConsume> specification = new Specification<StatisticConsume>() {
            @Override
            public Predicate toPredicate(Root<StatisticConsume> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(statisticConsume.getRegisterId()!=null) {
                    predicates.add(criteriaBuilder.equal(root.get("registerId"), statisticConsume.getRegisterId()));
                }
                if(statisticConsume.getUpdateTime()!=null) {
                    predicates.add(criteriaBuilder.like(root.get("updateTime"), statisticConsume.getUpdateTime()+"%"));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return specification;
    }
}
