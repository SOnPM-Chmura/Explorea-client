package com.sonpm_cloud.explorea.maps.route_creating;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sonpm_cloud.explorea.data_classes.DirectionsRoute;
import com.sonpm_cloud.explorea.data_classes.U;

import java.util.Arrays;

public class StraightLineDirectionsCreatingStrategy extends DirectionsCreatingStrategy {

    public StraightLineDirectionsCreatingStrategy(LatLng[] points, Context context) {
        super(points, context);
    }

    @Override
    public DirectionsRoute createDirectionsRoute() {
        float distance = 0f;
        PolylineOptions options;
        String city = "?";
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        try {
            options = new PolylineOptions().add(points[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
        bounds.include(points[0]);
        float[] temp = new float[3];
        for (int i = 1; i < points.length; i++) {
            options.add(points[i]);
            Location.distanceBetween(
                    points[i - 1].latitude, points[i - 1].longitude,
                    points[i].latitude, points[i].longitude,
                    temp
            );
            distance += temp[0];
            bounds.include(points[i]);
        }
        options.add(points[0]);
        Location.distanceBetween(
                points[points.length-1].latitude, points[points.length-1].longitude,
                points[0].latitude, points[0].longitude,
                temp
        );
        distance += temp[0];
        return new DirectionsRoute(
                -1,
                Arrays.asList(points),
                U.getCurrentMillis(),
                Arrays.asList(points),
                Arrays.asList(points),
                0f,
                (int) distance,
                (int) distance,
                (int) (((distance / 1000) / 5) * 60),
                (int) (((distance / 1000) / 20) * 60),
                city,
                bounds.build());
    }
}
