package com.sonpm_cloud.explorea.maps.route_creating;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.sonpm_cloud.explorea.data_classes.CachedDirectionsDAO;
import com.sonpm_cloud.explorea.data_classes.DirectionsRoute;

public class HybridDirectionsCreatingStrategy extends DirectionsCreatingStrategy {

    public HybridDirectionsCreatingStrategy(LatLng[] points, Context context) {
        super(points, context);
    }

    private DirectionsCreatingStrategy getPullingStrategy() {
        return new APIDirectionsCreatingStrategy(points, context);
    }

    @Override
    public DirectionsRoute createDirectionsRoute() {
/*       OOP, but not efficient - will require opening SQLite BD twice        */
//        DirectionsRoute ret = new CachedDirectionsCreatingStrategy(points, context).createDirectionsRoute();
        CachedDirectionsDAO cachedDirectionsDAO = new CachedDirectionsDAO(context);
        DirectionsRoute ret = cachedDirectionsDAO.getCDRorNull(points);
        if (ret == null) {
            ret = getPullingStrategy().createDirectionsRoute();
            if (ret == null) return null;
            cachedDirectionsDAO.insertCR(ret);
        }
        cachedDirectionsDAO.finish();
        return ret;
    }
}
