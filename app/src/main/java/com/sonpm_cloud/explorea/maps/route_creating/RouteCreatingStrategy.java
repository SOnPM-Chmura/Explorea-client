package com.sonpm_cloud.explorea.maps.route_creating;

import com.google.android.gms.maps.model.LatLng;
import com.sonpm_cloud.explorea.data_classes.DirectionsRoute;

public abstract class RouteCreatingStrategy {

    LatLng[] points;

    private RouteCreatingStrategy() {}

    public RouteCreatingStrategy(LatLng[] points) {
        this.points = points;
    }

    public abstract DirectionsRoute createDirectionsRoute();

    public static RouteCreatingStrategy getRecomendedStrategy(LatLng[] points) {
        return new StraightLineRouteCreatingStrategy(points);
    }
}
