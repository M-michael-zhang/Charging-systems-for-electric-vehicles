package cn.zy.charg.bean;

import java.util.Date;

public class Trans {
    private String serialNo;

    private Integer uid;

    private Integer pid;

    private Date transTime;

    private String type;

    private String status;

    private Date endTime;

    private Double costTime;

    private Double amount;

    private String reserve1;

    private String reserve2;

    public Trans() {
    }

    public Trans(String serialNo, Integer uid, Integer pid, Date transTime, String type, String status, Date endTime, Double costTime, Double amount, String reserve1, String reserve2) {
        this.serialNo = serialNo;
        this.uid = uid;
        this.pid = pid;
        this.transTime = transTime;
        this.type = type;
        this.status = status;
        this.endTime = endTime;
        this.costTime = costTime;
        this.amount = amount;
        this.reserve1 = reserve1;
        this.reserve2 = reserve2;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo == null ? null : serialNo.trim();
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Date getTransTime() {
        return transTime;
    }

    public void setTransTime(Date transTime) {
        this.transTime = transTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Double getCostTime() {
        return costTime;
    }

    public void setCostTime(Double costTime) {
        this.costTime = costTime;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
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
}