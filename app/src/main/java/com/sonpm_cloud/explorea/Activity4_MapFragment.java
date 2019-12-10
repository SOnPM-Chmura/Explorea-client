package com.sonpm_cloud.explorea;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.sonpm_cloud.explorea.data_classes.DirectionsRoute;
import com.sonpm_cloud.explorea.data_classes.Route;
import com.sonpm_cloud.explorea.data_classes.U;
import com.sonpm_cloud.explorea.maps.route_creating.RouteCreatingStrategy;

import java.util.List;

public class Activity4_MapFragment
        extends Fragment
        implements OnMapReadyCallback {

    private Route route;
    private DirectionsRoute directionsRoute;

    private MapView mapView;
    private GoogleMap googleMap;

    private String getMapViewBundleKey() { return "MapViewBundleKey3"; }
    private int getMapViewId() { return R.id.map_view3; }

    private Activity4_MapFragment() {}

    public Activity4_MapFragment(@NonNull Route route) {
        this.route = route;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null)
            mapViewBundle = savedInstanceState.getBundle(getMapViewBundleKey());

        mapView = requireView().findViewById(getMapViewId());
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_activity4__map,
                container,
                false);
    }

    public enum By {
        Foot, Bike
    }

    public void launchMap(By what) {
        String url1 = "https://www.google.pl/maps/dir/";
        String url2 = "data=!3m1!4b1!4m10!4m9!1m3!2m2!1d20!2d52!1m3!2m2!1d20!2d51!";
        String url3 = "";
        if (what == By.Foot) url3 = "3e1";
        if (what == By.Bike) url3 = "3e2";
        StringBuilder url = new StringBuilder(url1);

        if (what == By.Foot)
            for (LatLng coord : PolyUtil.decode(directionsRoute.encodedDirectionsByFoot))
                url.append(coord.latitude).append(",+").append(coord.longitude).append("/");
        if (what == By.Bike)
            for (LatLng coord : PolyUtil.decode(directionsRoute.encodedDirectionsByBike))
                url.append(coord.latitude).append(",+").append(coord.longitude).append("/");
        url.append(url2).append(url3);
        Log.e("Created url", url.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        float dp = 32;
        this.googleMap = googleMap;

        this.googleMap.setOnMarkerClickListener(marker -> true);

        List<LatLng> decodeRou = PolyUtil.decode(route.encodedRoute);
        for (LatLng marker : decodeRou)
            googleMap.addMarker(new MarkerOptions().position(marker));
        LatLngBounds.Builder bounds = LatLngBounds.builder();
        for (LatLng marker : decodeRou) bounds.include(marker);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(),
                (int) U.dp_px(dp, requireContext())));
        new DirectionsGetTask(this, (int) U.dp_px(dp, requireContext()))
                .execute(decodeRou.toArray(new LatLng[0]));
    }

    private static class DirectionsGetTask
            extends AsyncTask<LatLng, Void, Pair<Pair<PolylineOptions, PolylineOptions>, LatLngBounds>> {

        Activity4_MapFragment fragment;
        int padding;

        DirectionsGetTask(Activity4_MapFragment fragment,
                          int boundsPadding) {
            this.fragment = fragment;
            this.padding = boundsPadding;
        }

        @Override
        protected Pair<Pair<PolylineOptions, PolylineOptions>, LatLngBounds> doInBackground(LatLng... latLngs) {
            DirectionsRoute r = RouteCreatingStrategy
                    .getRecomendedStrategy(latLngs)
                    .createDirectionsRoute();
            LatLngBounds.Builder bounds = LatLngBounds.builder();
            fragment.directionsRoute = r;
            List<LatLng> decodeDirF = PolyUtil.decode(r.encodedDirectionsByFoot);
            List<LatLng> decodeDirB = PolyUtil.decode(r.encodedDirectionsByFoot);
            PolylineOptions polylineOptionsFoot = new PolylineOptions()
                    .addAll(decodeDirF).color(R.color.routeFoot);
            for (LatLng marker : decodeDirF) bounds.include(marker);
            PolylineOptions polylineOptionsBike = new PolylineOptions()
                    .addAll(decodeDirB).color(R.color.routeBike);
            for (LatLng marker : decodeDirB) bounds.include(marker);

            return Pair.create(
                    Pair.create(polylineOptionsFoot, polylineOptionsBike),
                            bounds.build());
        }

        @Override
        protected void onPostExecute(Pair<Pair<PolylineOptions, PolylineOptions>, LatLngBounds> result) {
            fragment.googleMap.addPolyline(result.first.first);
            fragment.googleMap.addPolyline(result.first.second);
            fragment.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(result.second, padding));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
