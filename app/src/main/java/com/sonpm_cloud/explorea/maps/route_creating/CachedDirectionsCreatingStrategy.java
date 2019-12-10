package com.sonpm_cloud.explorea.maps.route_creating;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.sonpm_cloud.explorea.data_classes.CachedDirectionsDAO;
import com.sonpm_cloud.explorea.data_classes.DirectionsRoute;

public class CachedDirectionsCreatingStrategy extends DirectionsCreatingStrategy {

    public CachedDirectionsCreatingStrategy(LatLng[] points, Context context) {
        super(points, context);
    }

    @Override
    public DirectionsRoute createDirectionsRoute() {
        return new CachedDirectionsDAO(context).getCDRorNull(points);
    }
}
