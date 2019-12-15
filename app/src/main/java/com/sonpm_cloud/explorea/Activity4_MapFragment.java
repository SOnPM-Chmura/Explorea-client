package com.sonpm_cloud.explorea;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.sonpm_cloud.explorea.data_classes.APIDirectionsDAO;
import com.sonpm_cloud.explorea.data_classes.DirectionsRoute;
import com.sonpm_cloud.explorea.data_classes.Route;
import com.sonpm_cloud.explorea.data_classes.U;
import com.sonpm_cloud.explorea.maps.route_creating.DirectionsCreatingStrategy;

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

    public void launchMap(APIDirectionsDAO.By what) {
        LatLng[] directions;
        if (what == APIDirectionsDAO.By.Foot) directions = PolyUtil.decode(directionsRoute.encodedDirectionsByFoot)
                                                                   .toArray(new LatLng[0]);
        else if(what == APIDirectionsDAO.By.Bike) directions = PolyUtil.decode(directionsRoute.encodedDirectionsByBike)
                                                                       .toArray(new LatLng[0]);
        else return;
        String url = APIDirectionsDAO.createGoogleNavigationURL(directions, what);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
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
            DirectionsRoute r = DirectionsCreatingStrategy
                    .getRecommendedStrategy(latLngs, fragment.requireContext())
                    .createDirectionsRoute();
            fragment.directionsRoute = r;
            List<LatLng> decodeDirF = PolyUtil.decode(r.encodedDirectionsByFoot);
            List<LatLng> decodeDirB = PolyUtil.decode(r.encodedDirectionsByFoot);
            PolylineOptions polylineOptionsFoot = new PolylineOptions()
                    .addAll(decodeDirF).color(R.color.routeFoot);
            PolylineOptions polylineOptionsBike = new PolylineOptions()
                    .addAll(decodeDirB).color(R.color.routeBike);

            return Pair.create(
                    Pair.create(polylineOptionsFoot, polylineOptionsBike),
                            r.bounds);
        }

        @Override
        protected void onPostExecute(Pair<Pair<PolylineOptions, PolylineOptions>, LatLngBounds> result) {
            if (result == null) {
                Toast.makeText(fragment.requireContext(),
                               fragment.getString(R.string.directions_null),
                               Toast.LENGTH_LONG).show();
                return;
            }
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
