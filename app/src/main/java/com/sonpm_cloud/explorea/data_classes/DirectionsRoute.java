package com.sonpm_cloud.explorea.data_classes;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public class DirectionsRoute extends Route {

    public final String encodedDirectionsByFoot;
    public final String encodedDirectionsByBike;
    public final long queryTime;
    public final LatLngBounds bounds;

    public DirectionsRoute(
            long id,
            String encodedRoute,
            long queryTime,
            String encodedDirectionsByFoot,
            String encodedDirectionsByBike,
            float avgRating,
            int lengthByFoot,
            int lengthByBike,
            int timeByFoot,
            int timeByBike,
            String city,
            LatLngBounds bounds) {
        super(id, encodedRoute, avgRating, lengthByFoot, lengthByBike, timeByFoot, timeByBike, city);
        this.encodedDirectionsByFoot = encodedDirectionsByFoot;
        this.encodedDirectionsByBike = encodedDirectionsByBike;
        this.queryTime = queryTime;
        this.bounds = bounds;
    }

    public DirectionsRoute(
            long id,
            List<LatLng> encodedRoute,
            long queryTime,
            List<LatLng> encodedDirectionsByFoot,
            List<LatLng> encodedDirectionsByBike,
            float avgRating,
            int lengthByFoot,
            int lengthByBike,
            int timeByFoot,
            int timeByBike,
            String city,
            LatLngBounds bounds) {
        super(id, encodedRoute, avgRating, lengthByFoot, lengthByBike, timeByFoot, timeByBike, city);
        this.encodedDirectionsByFoot = tryEncode(encodedDirectionsByFoot);
        this.encodedDirectionsByBike = tryEncode(encodedDirectionsByBike);
        this.queryTime = queryTime;
        this.bounds = bounds;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return "{\n" +
                "id: " + id + ",\n" +
                "encodedRoute: " + encodedRoute + ",\n" +
                "hex encodedRoute: " + hexEncode(encodedRoute) + ",\n" +
                "queryTime: " + queryTime + ",\n" +
                "encodedDirectionsByFoot: " + encodedDirectionsByFoot + ",\n" +
                "encodedDirectionsByBike: " + encodedDirectionsByBike + ",\n" +
                "avgRating: " + String.format("%.1f", avgRating) + ",\n" +
                "lengthByFoot: " + lengthByFoot + ",\n" +
                "lengthByBike: " + lengthByBike + ",\n" +
                "timeByFoot: " + timeByFoot + ",\n" +
                "timeByBike: " + timeByBike + ",\n" +
                "city: " + city + ",\n" +
                "bounds: " + bounds.toString() + "\n}";
    }
}
