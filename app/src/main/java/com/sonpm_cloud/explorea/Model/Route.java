package com.sonpm_cloud.explorea.Model;

public class Route {


    private int id;
    private String codedRoute;
    private double avgRating;
    private int lengthByFoot;
    private int lengthByBike;
    private int timeByFoot;
    private int timeByBike;
    private String city;

    public Route(int id, String codedRoute, double avgRating, int lengthByFoot, int lengthByBike, int timeByFoot, int timeByBike, String city) {
        this.id = id;
        this.codedRoute = codedRoute;
        this.avgRating = avgRating;
        this.lengthByFoot = lengthByFoot;
        this.lengthByBike = lengthByBike;
        this.timeByFoot = timeByFoot;
        this.timeByBike = timeByBike;
        this.city = city;
    }

    public Integer getLengthByFoot() {
        return lengthByFoot;
    }

    public void setLengthByFoot(Integer lengthByFoot) {
        this.lengthByFoot = lengthByFoot;
    }

    public Integer getLengthByBike() {
        return lengthByBike;
    }

    public void setLengthByBike(Integer lengthByBike) {
        this.lengthByBike = lengthByBike;
    }

    public Integer getTimeByFoot() {
        return timeByFoot;
    }

    public void setTimeByFoot(Integer timeByFoot) {
        this.timeByFoot = timeByFoot;
    }

    public Integer getTimeByBike() {
        return timeByBike;
    }

    public void setTimeByBike(Integer timeByBike) {
        this.timeByBike = timeByBike;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodedRoute() {
        return codedRoute;
    }

    public void setCodedRoute(String codedRoute) {
        this.codedRoute = codedRoute;
    }

    public Double getAverageRating() {
        return avgRating;
    }

    public void setAverageRating(Double averageRating) {
        this.avgRating = averageRating;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
