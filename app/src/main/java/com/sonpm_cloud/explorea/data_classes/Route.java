package com.sonpm_cloud.explorea.data_classes;

import android.annotation.SuppressLint;

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
        if (avgRating < 0 || avgRating > 5)
            throw th("Rating value must be between 0 and 5");
        this.avgRating = avgRating;
        if (lengthByFoot < 0 || lengthByFoot > 10000)
            throw th("Length by foot value must be between 0 and 10000");
        this.lengthByFoot = lengthByFoot;
        if (lengthByBike < 0 || lengthByBike > 30000)
            throw th("Length by bike value must be between 0 and 30000");
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
        this(id, tryEncode(decodedRoute), avgRating,
                lengthByFoot, lengthByBike, timeByFoot, timeByBike, city);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return "{\n" +
                "id: " + id + ",\n" +
                "encodedRoute: " + encodedRoute + ",\n" +
                "hex encodedRoute: " + hexEncode(encodedRoute) + ",\n" +
                "avgRating: " + String.format("%.1f", avgRating) + ",\n" +
                "lengthByFoot: " + lengthByFoot + ",\n" +
                "lengthByBike: " + lengthByBike + ",\n" +
                "timeByFoot: " + timeByFoot + ",\n" +
                "timeByBike: " + timeByBike + ",\n" +
                "city: " + city + "\n}";
    }

    public static String hexEncode(String decoded) {
        StringBuilder ret = new StringBuilder();
        char[] arr =  decoded.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            short s = (short) arr[i];
            if (s < 20 || s > 126) throw th("Cannot encode character at " +
                    i + ", not ASCII writing character");
            ret.append(String.format("%x", s));
        }
        return ret.toString();
    }

    public static String hexDecode(String encoded) {
        StringBuilder ret = new StringBuilder();
        if (encoded.length() % 2 != 0)
            throw th("String length illegal for percent-encoded String");
        String s;
        char c;
        for (int i = 0; i < encoded.length()/2; i++) {
            s = encoded.substring(i * 2, (i + 1) * 2);
            try {
                c = (char) Short.parseShort(s, 16);
            } catch (NumberFormatException ignore) {
                throw th("Cannot decode character at " + (i * 2) +
                        ", not ASCII hex value");
            }
            if (c < 20 || c > 126) throw th("Cannot encode character at " +
                    (i * 2) + ", not ASCII writing character");
            ret.append(c);
        }
        return ret.toString();
    }

    private static String tryEncode(List<LatLng> decoded) {
        if (decoded.size() > 25)
            throw th("Routes cannot contain more than 25 points");
        return PolyUtil.encode(decoded);
    }

    private static IllegalArgumentException th(String msg) {
        return new IllegalArgumentException(msg);
    }
}
