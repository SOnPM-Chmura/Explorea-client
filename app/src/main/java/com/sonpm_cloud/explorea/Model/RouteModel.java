package com.sonpm_cloud.explorea.Model;

public class RouteModel {


    private int id;
    private String codedRoute;
    private double avgRating;
    private int lengthByFoot;
    private int lengthByBike;
    private int timeByFoot;
    private int timeByBike;
    private String city;

    public RouteModel(int id, String codedRoute, double avgRating, int lengthByFoot, int lengthByBike, int timeByFoot, int timeByBike, String city) {
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

    public Integer getLengthByBike() {
        return lengthByBike;
    }

    public Integer getTimeByFoot() {
        return timeByFoot;
    }

    public Integer getTimeByBike() {
        return timeByBike;
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

    public Double getAverageRating() {
        return avgRating;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
