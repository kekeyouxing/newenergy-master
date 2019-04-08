package newenergy.db.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recharge_record")
public class NewenergyOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 14)
    private String register_id;//登记号,设备号

    private Integer amount;//充值金额

    private LocalDateTime recharge_time;//充值时间

    private double recharge_volume;//充值流量

    private double remain_volume;//剩余流量

    private double updated_volume;//可用流量/新增流量

    private String user_name;//批量充值人的名字   默认:微信充值时为用户昵称

    private String user_phone;//充值人的手机号   默认：微信充值时为空

    private Integer state;//订单状态（0正常，1作废）

    private Integer delegate;//是否代充(0非代充，1代充)

    private String transaction_id;//微信订单号(个人微信充值后返回的订单号，用于退款，若为批量充值此项为空)

    private LocalDateTime safe_changed_time;//安全属性：添加事件  默认：LocalDateTime.now()

    private Integer safe_changed_userid;//安全属性：添加人id

    private Integer safe_delete;//安全属性：是否删除，1：删除，0：未删除  默认0

    private Integer safe_parent;//安全属性：上次修改记录的id   默认：null

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRegister_id() {
        return register_id;
    }

    public void setRegister_id(String register_id) {
        this.register_id = register_id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public LocalDateTime getRecharge_time() {
        return recharge_time;
    }

    public void setRecharge_time(LocalDateTime recharge_time) {
        this.recharge_time = recharge_time;
    }

    public double getRecharge_volume() {
        return recharge_volume;
    }

    public void setRecharge_volume(double recharge_volume) {
        this.recharge_volume = recharge_volume;
    }

    public double getRemain_volume() {
        return remain_volume;
    }

    public void setRemain_volume(double remain_volume) {
        this.remain_volume = remain_volume;
    }

    public double getUpdated_volume() {
        return updated_volume;
    }

    public void setUpdated_volume(double updated_volume) {
        this.updated_volume = updated_volume;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getDelegate() {
        return delegate;
    }

    public void setDelegate(Integer delegate) {
        this.delegate = delegate;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public LocalDateTime getSafe_changed_time() {
        return safe_changed_time;
    }

    public void setSafe_changed_time(LocalDateTime safe_changed_time) {
        this.safe_changed_time = safe_changed_time;
    }

    public Integer getSafe_changed_userid() {
        return safe_changed_userid;
    }

    public void setSafe_changed_userid(Integer safe_changed_userid) {
        this.safe_changed_userid = safe_changed_userid;
    }

    public Integer getSafe_delete() {
        return safe_delete;
    }

    public void setSafe_delete(Integer safe_delete) {
        this.safe_delete = safe_delete;
    }

    public Integer getSafe_parent() {
        return safe_parent;
    }

    public void setSafe_parent(Integer safe_parent) {
        this.safe_parent = safe_parent;
    }
}
