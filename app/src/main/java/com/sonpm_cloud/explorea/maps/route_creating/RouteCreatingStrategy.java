package com.sonpm_cloud.explorea.maps.route_creating;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.sonpm_cloud.explorea.data_classes.DirectionsRoute;

public abstract class RouteCreatingStrategy {

    LatLng[] points;
    Context context;

    public RouteCreatingStrategy(LatLng[] points, Context context) {
        this.points = points;
        this.context = context;
    }

    public abstract DirectionsRoute createDirectionsRoute();

    public static RouteCreatingStrategy getRecomendedStrategy(LatLng[] points, Context context) {
        return new StraightLineRouteCreatingStrategy(points, context);
    }
}
