package com.sonpm_cloud.explorea.maps.route_creating;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.sonpm_cloud.explorea.data_classes.DirectionsRoute;

public abstract class DirectionsCreatingStrategy {

    LatLng[] points;
    Context context;

    public DirectionsCreatingStrategy(LatLng[] points, Context context) {
        this.points = points;
        this.context = context;
    }

    public abstract DirectionsRoute createDirectionsRoute();

    public static DirectionsCreatingStrategy getRecommendedStrategy(LatLng[] points, Context context) {
        return new StraightLineDirectionsCreatingStrategy(points, context);
    }
}
