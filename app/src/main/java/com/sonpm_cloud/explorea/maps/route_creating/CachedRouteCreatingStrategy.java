package com.sonpm_cloud.explorea.maps.route_creating;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.sonpm_cloud.explorea.data_classes.CachedRoutesDAO;
import com.sonpm_cloud.explorea.data_classes.DirectionsRoute;

public class CachedRouteCreatingStrategy extends RouteCreatingStrategy {

    public CachedRouteCreatingStrategy(LatLng[] points, Context context) {
        super(points, context);
    }

    @Override
    public DirectionsRoute createDirectionsRoute() {
        return new CachedRoutesDAO(context).getCRorNull(points);
    }
}
