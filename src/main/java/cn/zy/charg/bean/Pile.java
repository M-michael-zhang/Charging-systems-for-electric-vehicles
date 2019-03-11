package cn.zy.charg.bean;

public class Pile {
    private Integer id;

    private Double locationx;

    private Double locationy;

    private Integer state;

    public Pile() {
    }

    public Pile(Integer id, Double locationx, Double locationy, Integer state) {
        this.id = id;
        this.locationx = locationx;
        this.locationy = locationy;
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getLocationx() {
        return locationx;
    }

    public void setLocationx(Double locationx) {
        this.locationx = locationx;
    }

    public Double getLocationy() {
        return locationy;
    }

    public void setLocationy(Double locationy) {
        this.locationy = locationy;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}