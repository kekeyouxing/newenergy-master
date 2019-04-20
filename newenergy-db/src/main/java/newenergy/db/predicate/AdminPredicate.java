package newenergy.db.predicate;

import java.util.List;

/**
 * Created by HUST Corey on 2019-04-20.
 */
public class AdminPredicate {
    private String realName;
    List<Integer> ids;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }
}
