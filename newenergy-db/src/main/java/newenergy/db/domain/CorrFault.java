package newenergy.db.domain;

import javax.persistence.*;

/**
 * Created by HUST Corey on 2019-03-27.
 */
@Entity
@Table(
name = "corr_fault",
indexes = {
        @Index(columnList = "faultNum")
    }
)
public class CorrFault {
    @Id
    @GeneratedValue
    private Integer id;
    private Integer faultNum;
    private String faultDtl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFaultNum() {
        return faultNum;
    }

    public void setFaultNum(Integer faultNum) {
        this.faultNum = faultNum;
    }

    public String getFaultDtl() {
        return faultDtl;
    }

    public void setFaultDtl(String faultDtl) {
        this.faultDtl = faultDtl;
    }
}
