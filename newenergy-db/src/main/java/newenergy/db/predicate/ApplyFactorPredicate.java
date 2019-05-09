package newenergy.db.predicate;

import java.util.List;

/**
 * Created by HUST Corey on 2019-04-18.
 */
public class ApplyFactorPredicate {
    private String plotDtl;
    private Integer state;
    private List<String> plots;

    public List<String> getPlots() {
        return plots;
    }

    public void setPlots(List<String> plots) {
        this.plots = plots;
    }

    public String getPlotDtl() {
        return plotDtl;
    }

    public void setPlotDtl(String plotDtl) {
        this.plotDtl = plotDtl;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
