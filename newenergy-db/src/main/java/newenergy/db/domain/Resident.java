package newenergy.db.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users_register_info")
public class Resident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //登记号
    @Column(unique = true, length = 14)
    private String registerId;

    //用户姓名
    private String userName;

    //小区编号，见数据对应表-小区
    @Column(length = 2)
    private String plotNum;

    //地址编号，见数据对应表-地址
    @Column(length = 5)
    private String addressNum;

    //房间号
    @Column(length = 4)
    private String roomNum;

    //机型编号，见数据对应表-机型
    @Column(length = 2)
    private String typeNum;

    //装机序号，第几台机器（0-z）
    @Column(length = 1)
    private String deviceSeq;

    //所属机房编号，见数据对应表-机房
    @Column(length = 2)
    private String pumpNum;

    //联系电话
    private String phone;

    //房间面积
    private Double area;

    //额定流量，单位T/h
    private Double ratedFlow;

    //机器编码
    @Column(unique = true)
    private String deviceNum;

    //购机日期
    private LocalDateTime buyTime;

    //安装设备日期
    private LocalDateTime installTime;

    //验收设备日期
    private LocalDateTime receiveTime;

    //微信openid
    private String openid;

    //流量预警阈值
    @Column(columnDefinition = "int default 10")
    private Integer threshold;

    //安全属性：添加时间
    private LocalDateTime safeChangedTime;

    //安全属性： 添加人id
    private Integer safeChangedUserid;

    //安全属性：是否删除，1删除，0未删除
    @Column(columnDefinition = "tinyint default 0")
    private Integer safeDelete;

    //安全属性：上次修改记录的id
    private Integer safeParent;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }

    public void initRegisterId() {
        this.registerId = this.getAddressNum() + this.getRoomNum() + this.getTypeNum() + this.getDeviceSeq()
                + this.getPumpNum();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPlotNum() {
        return plotNum;
    }

    public void setPlotNum(String plotNum) {
        this.plotNum = plotNum;
    }

    public String getAddressNum() {
        return addressNum;
    }

    public void setAddressNum(String addressNum) {
        this.addressNum = addressNum;
    }

    public String getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(String roomNum) {
        this.roomNum = roomNum;
    }

    public String getTypeNum() {
        return typeNum;
    }

    public void setTypeNum(String typeNum) {
        this.typeNum = typeNum;
    }

    public String getDeviceSeq() {
        return deviceSeq;
    }

    public void setDeviceSeq(String deviceSeq) {
        this.deviceSeq = deviceSeq;
    }

    public String getPumpNum() {
        return pumpNum;
    }

    public void setPumpNum(String pumpNum) {
        this.pumpNum = pumpNum;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Double getRatedFlow() {
        return ratedFlow;
    }

    public void setRatedFlow(Double ratedFlow) {
        this.ratedFlow = ratedFlow;
    }

    public String getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        this.deviceNum = deviceNum;
    }

    public LocalDateTime getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(LocalDateTime buyTime) {
        this.buyTime = buyTime;
    }

    public LocalDateTime getInstallTime() {
        return installTime;
    }

    public void setInstallTime(LocalDateTime installTime) {
        this.installTime = installTime;
    }

    public LocalDateTime getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(LocalDateTime receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public LocalDateTime getSafeChangedTime() {
        return safeChangedTime;
    }

    public void setSafeChangedTime(LocalDateTime safeChangedTime) {
        this.safeChangedTime = safeChangedTime;
    }

    public Integer getSafeChangedUserid() {
        return safeChangedUserid;
    }

    public void setSafeChangedUserid(Integer safeChangedUserid) {
        this.safeChangedUserid = safeChangedUserid;
    }

    public Integer getSafeDelete() {
        return safeDelete;
    }

    public void setSafeDelete(Integer safeDelete) {
        this.safeDelete = safeDelete;
    }

    public Integer getSafeParent() {
        return safeParent;
    }

    public void setSafeParent(Integer safeParent) {
        this.safeParent = safeParent;
    }
}
