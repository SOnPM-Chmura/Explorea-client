package com.sonpm_cloud.explorea.maps.route_creating;

import com.google.android.gms.maps.model.LatLng;
import com.sonpm_cloud.explorea.data_classes.DirectionsRoute;

public class EmptyRouteCreatingStrategy extends RouteCreatingStrategy {

    EmptyRouteCreatingStrategy(LatLng[] points) { super(points); }

    @Override
    public DirectionsRoute createDirectionsRoute() { return null; }
}
