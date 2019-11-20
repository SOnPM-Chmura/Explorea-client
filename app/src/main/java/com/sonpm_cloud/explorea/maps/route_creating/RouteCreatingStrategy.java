package com.sonpm_cloud.explorea.maps.route_creating;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class RouteCreatingStrategy {

    LatLng[] points;

    private RouteCreatingStrategy() {}

    public RouteCreatingStrategy(LatLng[] points) {
        this.points = points;
    }

    public abstract PolylineRoute createPolylineRoute();

    public static RouteCreatingStrategy getRecomendedStrategy(LatLng[] points) {
        return new StraightLineRouteCreatingStrategy(points);
    }

    public static class PolylineRoute {

        public final float length;
        public final PolylineOptions polylineOptions;

        public PolylineRoute(float length, PolylineOptions polylineOptions) {
            this.length = length;
            this.polylineOptions = polylineOptions;
        }
    }
}
