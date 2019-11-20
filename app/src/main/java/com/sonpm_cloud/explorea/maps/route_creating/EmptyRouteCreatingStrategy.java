package com.sonpm_cloud.explorea.maps.route_creating;

import com.google.android.gms.maps.model.LatLng;

public class EmptyRouteCreatingStrategy extends RouteCreatingStrategy {

    EmptyRouteCreatingStrategy(LatLng[] points) { super(points); }

    @Override
    public PolylineRoute createPolylineRoute() { return null; }
}
