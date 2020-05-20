package cn.zy.charg.bean;


import java.util.Date;

public class Pile {
    private Integer cid;

    private Integer id;

    private String type;

    private String address;

    private Double locationX;

    private Double locationY;

    private Double voltage;

    private Double electricity;

    private String status;

    private Date createTime;

    private String qrcode;

    private String qrcodePath;

    private Double rate;

    private String reserve1;

    private String reserve2;

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Double getLocationX() {
        return locationX;
    }

    public void setLocationX(Double locationX) {
        this.locationX = locationX;
    }

    public Double getLocationY() {
        return locationY;
    }

    public void setLocationY(Double locationY) {
        this.locationY = locationY;
    }

    public Double getVoltage() {
        return voltage;
    }

    public void setVoltage(Double voltage) {
        this.voltage = voltage;
    }

    public Double getElectricity() {
        return electricity;
    }

    public void setElectricity(Double electricity) {
        this.electricity = electricity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode == null ? null : qrcode.trim();
    }

    public String getQrcodePath() {
        return qrcodePath;
    }

    public void setQrcodePath(String qrcodePath) {
        this.qrcodePath = qrcodePath == null ? null : qrcodePath.trim();
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getReserve1() {
        return reserve1;
    }

    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1 == null ? null : reserve1.trim();
    }

    public String getReserve2() {
        return reserve2;
    }

    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2 == null ? null : reserve2.trim();
    }

    @Override
    public String toString() {
        return "Pile{" +
                "cid=" + cid +
                ", id=" + id +
                ", type='" + type + '\'' +
                ", address='" + address + '\'' +
                ", locationX=" + locationX +
                ", locationY=" + locationY +
                ", voltage=" + voltage +
                ", electricity=" + electricity +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", qrcode='" + qrcode + '\'' +
                ", qrcodePath='" + qrcodePath + '\'' +
                ", rate=" + rate +
                ", reserve1='" + reserve1 + '\'' +
                ", reserve2='" + reserve2 + '\'' +
                '}';
    }
}