package com.sonpm_cloud.explorea.maps.route_creating;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Arrays;

public class StraightLineRouteCreatingStrategy extends RouteCreatingStrategy {

    public StraightLineRouteCreatingStrategy(LatLng[] points) { super(points); }

    @Override
    public PolylineRoute createPolylineRoute() {
        float distance = 0f;
        PolylineOptions options;
        try {
            options = new PolylineOptions().add(points[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            return new PolylineRoute(0f, null);
        }
        float[] temp = new float[3];
        for (int i = 1; i < points.length; i++) {
            options.add(points[i]);
            Location.distanceBetween(
                    points[i - 1].latitude, points[i - 1].longitude,
                    points[i].latitude, points[i].longitude,
                    temp
            );
            distance += temp[0];
        }
        options.add(points[0]);
        Location.distanceBetween(
                points[points.length-1].latitude, points[points.length-1].longitude,
                points[0].latitude, points[0].longitude,
                temp
        );
        distance += temp[0];
        return new PolylineRoute(distance, options);
    }
}
