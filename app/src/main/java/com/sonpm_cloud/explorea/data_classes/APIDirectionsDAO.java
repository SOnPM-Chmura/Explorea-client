package com.sonpm_cloud.explorea.data_classes;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.PolyUtil;

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
                route.id, route.avgRating);
    }

    @Nullable
    @WorkerThread
    public DirectionsRoute getDRorNull(final LatLng[] points) {
        return getDRorNull(points, null, null);
    }

    @WorkerThread
    private DirectionsRoute getDRorNull(@NonNull LatLng[] route,
                                        @Nullable Long id,
                                        @Nullable Float avgRating) {

        String url = createAPIDirectionsURL(route);


        RequestFuture<JSONObject> futureDir = RequestFuture.newFuture();
        JsonObjectRequest requestDir = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                futureDir,
                futureDir);
        Volley.newRequestQueue(context).add(requestDir);
        try {
            JSONObject response = futureDir.get(90, TimeUnit.SECONDS);

            String encodedRoute = Route.hexDecode(response.getString("encodedRoute"));
            long queryTime = response.getLong("queryTime");
            String encodedDirFoot = Route.hexDecode(response.getString("encodedDirectionsFoot"));
            String encodedDirBike = Route.hexDecode(response.getString("encodedDirectionsBike"));
            int distanceFoot = response.getInt("distanceFoot");
            int distanceBike = response.getInt("distanceBike");
            int timeFoot = response.getInt("timeFoot");
            int timeBike = response.getInt("timeBike");
            double ne_lat = response.getJSONObject("bounds").getJSONObject("northEast").getDouble("lat");
            double ne_lng = response.getJSONObject("bounds").getJSONObject("northEast").getDouble("lng");
            double sw_lat = response.getJSONObject("bounds").getJSONObject("southWest").getDouble("lat");
            double sw_lng = response.getJSONObject("bounds").getJSONObject("southWest").getDouble("lng");
            String city = response.getString("city");
            LatLngBounds bounds = LatLngBounds.builder()
                    .include(new LatLng(ne_lat, ne_lng))
                    .include(new LatLng(sw_lat, sw_lng))
                    .build();

            return new DirectionsRoute(
                    id == null ? -1 : id,
                    encodedRoute,
                    queryTime,
                    encodedDirFoot,
                    encodedDirBike,
                    avgRating == null ? 0 : avgRating,
                    distanceFoot,
                    distanceBike,
                    timeFoot,
                    timeBike,
                    city,
                    bounds
            );

        } catch (InterruptedException | TimeoutException | ExecutionException | JSONException e) {
            return null;
        }
    }

    public enum By {
        Foot, Bike
    }

    private String createAPIDirectionsURL(LatLng[] route) {
        String url = "https://explorea-server.azurewebsites.net/routes/directionsApi?encodedRoute=";
        url += Route.hexEncode(PolyUtil.encode(Arrays.asList(route)));
        return url;

    }

    public static String createGoogleNavigationURL(LatLng[] route, By what) {
        try {
            if (route.length < 2 || route.length > 25) {
                Log.e("createGoogleNavURL", "length wrong " + route.length);
                return null;
            }
            StringBuilder url = new StringBuilder("https://www.google.pl/maps/dir/?api=1");

            if (what == By.Foot) url.append("&travelmode=walking&waypoints=");
            else if (what == By.Bike) url.append("&travelmode=bicycling&waypoints=");
            else {
                Log.e("createGoogleNavURL", "what unknown " + String.valueOf(what));
                return null;
            }

            for (int i = 0; i < route.length-1; i++) {
                url.append(URLEncoder.encode((route[i].latitude + "," + route[i].longitude + "|"),
                        StandardCharsets.UTF_8.name()));
            }
            url.delete(url.length()-3, url.length());
            url.append("&destination=")
                    .append(URLEncoder.encode((route[route.length-1].latitude + "," + route[route.length-1].longitude),
                            StandardCharsets.UTF_8.name()));
            return url.toString();
        } catch (UnsupportedEncodingException e) {
            Log.e("createGoogleNavURL", "encoding wrong");
            return null;
        }
    }
}
