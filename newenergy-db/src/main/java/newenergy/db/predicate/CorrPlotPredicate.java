package newenergy.db.predicate;

import java.util.List;

/**
 * Created by HUST Corey on 2019-04-18.
 */
public class CorrPlotPredicate {
    private String plotNum;
    private String plotDtl;
    private List<String> plots;

    public List<String> getPlots() {
        return plots;
    }

    public void setPlots(List<String> plots) {
        this.plots = plots;
    }

    public String getPlotNum() {
        return plotNum;
    }

    public void setPlotNum(String plotNum) {
        this.plotNum = plotNum;
    }

    public String getPlotDtl() {
        return plotDtl;
    }

    public void setPlotDtl(String plotDtl) {
        this.plotDtl = plotDtl;
    }
}
