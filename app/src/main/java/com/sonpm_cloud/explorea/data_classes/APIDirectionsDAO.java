package com.sonpm_cloud.explorea.data_classes;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.PolyUtil;
import com.sonpm_cloud.explorea.maps.route_creating.StraightLineDirectionsCreatingStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class APIDirectionsDAO {

    Context context;

    public APIDirectionsDAO(Context context) { this.context = context; }

    @Nullable
    @WorkerThread
    public DirectionsRoute getDRorNull(final Route route) {
        return getDRorNull(PolyUtil.decode(route.encodedRoute).toArray(new LatLng[0]),
                           route.id, route.avgRating, route.city);
    }

    @Nullable
    @WorkerThread
    public DirectionsRoute getDRorNull(final LatLng[] points) {
        return getDRorNull(points, null, null, null);
    }

    @Deprecated
    @WorkerThread
    // Needs proper implementation after creating proxy to DirectionsAPI
    private DirectionsRoute getDRorNull(@NonNull LatLng[] route,
                                         @Nullable Long id,
                                         @Nullable Float avgRating,
                                         @Nullable String city) {
//        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?key=" +
//                                              getApiKey());
//        try {
//            try {
//                url.append("&origin=").append(URLEncoder.encode(route[0].latitude + "," + route[0].longitude,
//                                                                StandardCharsets.UTF_8.name()));
//            } catch (ArrayIndexOutOfBoundsException e) {
//                return null;
//            }
//            if (route.length > 2) {
//                url.append("&waypoints=");
//                for (int i = 1; i < route.length - 1; i++) {
//                    url.append(URLEncoder.encode("via:" + route[i].latitude + "," + route[i].longitude + "|",
//                                                 StandardCharsets.UTF_8.name()));
//                }
//                url.delete(url.length()-3, url.length());
//            } else if (route.length == 1) return null;
//            url.append("&destination=")
//               .append(URLEncoder.encode(route[route.length - 1].latitude + "," + route[route.length-1].longitude,
//                                                                 StandardCharsets.UTF_8.name()));
//
//            Log.e("url >>>>>>>>>>", url.toString());
//        } catch (UnsupportedEncodingException e) {
//            return null;
//        }
//        RequestFuture<JSONObject> futureFoot = RequestFuture.newFuture();
//        RequestFuture<JSONObject> futureBike = RequestFuture.newFuture();
//        JsonObjectRequest requestFoot = new JsonObjectRequest(Request.Method.GET,
//                                                              url.toString() + "&mode=walking",
//                                                              null,
//                                                              response -> System.out.println(response.toString()), error -> System.out.println(new String(error.networkResponse.data)));
//        JsonObjectRequest requestBike = new JsonObjectRequest(Request.Method.GET,
//                                                              url.toString() + "&mode=bicycling",
//                                                              null, futureBike, futureBike);
//        Volley.newRequestQueue(context).add(requestBike);
//        Volley.newRequestQueue(context).add(requestFoot);
//        try {
//            JSONObject responseBike = futureBike.get(90, TimeUnit.SECONDS);
//            JSONObject responseFoot = futureFoot.get(90, TimeUnit.SECONDS);
//
//            long queryTime = U.getCurrentMillis();
//
//
//            double bound_ne_latf = responseFoot.getJSONArray("routes")
//                                               .getJSONObject(0)
//                                               .getJSONObject("bounds")
//                                               .getJSONObject("northeast")
//                                               .getDouble("lat");
//
//            double bound_ne_lngf = responseFoot.getJSONArray("routes")
//                                               .getJSONObject(0)
//                                               .getJSONObject("bounds")
//                                               .getJSONObject("northeast")
//                                               .getDouble("lng");
//
//            double bound_sw_latf = responseFoot.getJSONArray("routes")
//                                               .getJSONObject(0)
//                                               .getJSONObject("bounds")
//                                               .getJSONObject("southwest")
//                                               .getDouble("lat");
//
//            double bound_sw_lngf = responseFoot.getJSONArray("routes")
//                                               .getJSONObject(0)
//                                               .getJSONObject("bounds")
//                                               .getJSONObject("southwest")
//                                               .getDouble("lng");
//
//
//            double bound_ne_latb = responseBike.getJSONArray("routes")
//                                               .getJSONObject(0)
//                                               .getJSONObject("bounds")
//                                               .getJSONObject("northeast")
//                                               .getDouble("lat");
//
//            double bound_ne_lngb = responseBike.getJSONArray("routes")
//                                               .getJSONObject(0)
//                                               .getJSONObject("bounds")
//                                               .getJSONObject("northeast")
//                                               .getDouble("lng");
//
//            double bound_sw_latb = responseBike.getJSONArray("routes")
//                                               .getJSONObject(0)
//                                               .getJSONObject("bounds")
//                                               .getJSONObject("southwest")
//                                               .getDouble("lat");
//
//            double bound_sw_lngb = responseBike.getJSONArray("routes")
//                                               .getJSONObject(0)
//                                               .getJSONObject("bounds")
//                                               .getJSONObject("southwest")
//                                               .getDouble("lng");
//
//            LatLngBounds bounds = new LatLngBounds(
//                    new LatLng(Math.min(bound_sw_latb, bound_sw_latf), Math.min(bound_sw_lngb, bound_sw_lngf)),
//                    new LatLng(Math.max(bound_ne_latb, bound_ne_latf), Math.max(bound_ne_lngb, bound_ne_lngf))
//            );
//
//            int disf = responseFoot.getJSONArray("routes")
//                                      .getJSONObject(0)
//                                      .getJSONArray("legs")
//                                      .getJSONObject(0)
//                                      .getJSONObject("distance")
//                                      .getInt("value");
//
//            int timf = responseFoot.getJSONArray("routes")
//                                   .getJSONObject(0)
//                                   .getJSONArray("legs")
//                                   .getJSONObject(0)
//                                   .getJSONObject("duration")
//                                   .getInt("value") / 60;
//
//            int disb = responseBike.getJSONArray("routes")
//                                   .getJSONObject(0)
//                                   .getJSONArray("legs")
//                                   .getJSONObject(0)
//                                   .getJSONObject("distance")
//                                   .getInt("value");
//
//            int timb = responseBike.getJSONArray("routes")
//                                   .getJSONObject(0)
//                                   .getJSONArray("legs")
//                                   .getJSONObject(0)
//                                   .getJSONObject("duration")
//                                   .getInt("value") / 60;
//
//            String dirf = responseFoot.getJSONArray("routes")
//                                      .getJSONObject(0)
//                                      .getJSONObject("overview_polyline")
//                                      .getString("points");
//
//            String dirb = responseBike.getJSONArray("routes")
//                                      .getJSONObject(0)
//                                      .getJSONObject("overview_polyline")
//                                      .getString("points");
//
//            return new DirectionsRoute(
//                    id == null ? -1 : id,
//                    PolyUtil.encode(Arrays.asList(route)),
//                    queryTime,
//                    dirf,
//                    dirb,
//                    avgRating == null ? 0 : avgRating,
//                    disf,
//                    disb,
//                    timf,
//                    timb,
//                    city == null ? "" : city,
//                    bounds
//            );
//        } catch (InterruptedException | TimeoutException | ExecutionException | JSONException e) {
//            Log.e("APIDirectionsDAO", "Exception");
//            e.printStackTrace();
//            return null;
//        }
        return new StraightLineDirectionsCreatingStrategy(route, context).createDirectionsRoute();
    }

    private String getApiKey() {
        try {
            ApplicationInfo appInf = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                                                                                    PackageManager.GET_META_DATA);
            Bundle meta = appInf.metaData;
            return meta.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            throw new NoSuchElementException("API Key may not be present");
        }
    }

    public enum By {
        Foot, Bike
    }

    private String createAPIDirectionsURL(LatLng[] route, By what, String apiKey) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?key=" + apiKey);
        try {
            try {
                url.append("&origin=").append(URLEncoder.encode(route[0].latitude + "," + route[0].longitude,
                                                                StandardCharsets.UTF_8.name()));
            } catch (ArrayIndexOutOfBoundsException e) {
                return null;
            }
            if (route.length > 2) {
                url.append("&waypoints=");
                for (int i = 1; i < route.length - 1; i++) {
                    url.append(URLEncoder.encode("via:" + route[i].latitude + "," + route[i].longitude + "|",
                                                 StandardCharsets.UTF_8.name()));
                }
                url.delete(url.length()-3, url.length());
            } else if (route.length == 1) return null;
            url.append("&destination=")
               .append(URLEncoder.encode(route[route.length - 1].latitude + "," + route[route.length-1].longitude,
                                         StandardCharsets.UTF_8.name()));
            if (what == By.Foot) {
                url.append("&mode=walking");
            } else
            if (what == By.Bike) {
                url.append("&mode=bicycling");
            } else {
                return null;
            }
            return url.toString();

        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String createGoogleNavigationURL(LatLng[] route, By what) {
        try {
            if (route.length < 2 || route.length > 25) return null;
            StringBuilder url = new StringBuilder("https://www.google.pl/maps/dir/?api=1&dir_action=navigate");

            if (what == By.Foot) url.append("&travelmode=walking&waypoints=");
            else if (what == By.Bike) url.append("&travelmode=walking&waypoints=");
            else return null;

            for (int i = 0; i < route.length-1; i++) {
                url.append(URLEncoder.encode((route[i].latitude + "," + route[i].longitude + "|"),
                                             StandardCharsets.UTF_8.name()));
            }
            url.delete(url.length()-3, url.length());
            url.append("&destination=")
               .append(URLEncoder.encode((route[route.length-1].latitude + "," + route[route.length-1].longitude + "|"),
                                         StandardCharsets.UTF_8.name()));
            return url.toString();
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
