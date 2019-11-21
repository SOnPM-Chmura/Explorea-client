package com.sonpm_cloud.explorea.data_classes;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.List;

public class Route {

    public final long id;
    public final String encodedRoute;
    public List<LatLng> decodedRoute() { return PolyUtil.decode(encodedRoute); }
    public final float avgRating;
    public final int lengthByFoot;
    public final int lengthByBike;
    public final int timeByFoot;
    public final int timeByBike;
    public final String city;

    public Route(
            long id,
            String encodedRoute,
            float avgRating,
            int lengthByFoot,
            int lengthByBike,
            int timeByFoot,
            int timeByBike,
            String city) {
        this.id = id;
        this.encodedRoute = encodedRoute;
        this.avgRating = avgRating;
        this.lengthByFoot = lengthByFoot;
        this.lengthByBike = lengthByBike;
        this.timeByFoot = timeByFoot;
        this.timeByBike = timeByBike;
        this.city = city;
    }

    public Route(
            long id,
            List<LatLng> decodedRoute,
            float avgRating,
            int lengthByFoot,
            int lengthByBike,
            int timeByFoot,
            int timeByBike,
            String city) {
        this(id, PolyUtil.encode(decodedRoute), avgRating,
                lengthByFoot, lengthByBike, timeByFoot, timeByBike, city);
    }

    @NonNull
    @Override
    public String toString() {
        return "{\n" +
                "id: " + id + ",\n" +
                "encodedRoute: " + encodedRoute + ",\n" +
                "avgRating: " + String.format("%.1f", avgRating) + ",\n" +
                "lengthByFoot: " + lengthByFoot + ",\n" +
                "lengthByBike: " + lengthByBike + ",\n" +
                "timeByFoot: " + timeByFoot + ",\n" +
                "timeByBike: " + timeByBike + ",\n" +
                "city: " + city + "\n}";
    }
}
