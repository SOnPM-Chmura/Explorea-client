package com.sonpm_cloud.explorea.maps.route_creating;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.sonpm_cloud.explorea.data_classes.DirectionsRoute;

public class EmptyRouteCreatingStrategy extends RouteCreatingStrategy {

    EmptyRouteCreatingStrategy(LatLng[] points, Context context) {
        super(points, context);
    }

    @Override
    public DirectionsRoute createDirectionsRoute() { return null; }
}
