package newenergy.db.repository;

import newenergy.db.domain.CorrPlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorrPlotRepository  extends JpaRepository<CorrPlot, Integer>, JpaSpecificationExecutor<CorrPlot> {
    public List<CorrPlot> findAllBySafeDelete(Integer safe_delete);
    public CorrPlot findByPlotDtlAndSafeDelete(String plot_dlt, Integer safe_delete);

    /**
     * 修改此方法添加safe_delete属性
     * @author yangq
     * @param plotNum
     * @param safe_delete
     * @return
     */
    //public CorrPlot findFirstByPlotNum(String plotNum);
    public CorrPlot findFirstByPlotNumAndSafeDelete(String plotNum,Integer safe_delete);
}
