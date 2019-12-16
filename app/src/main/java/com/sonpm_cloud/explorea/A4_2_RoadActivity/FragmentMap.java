package com.sonpm_cloud.explorea.A4_2_RoadActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.sonpm_cloud.explorea.R;
import com.sonpm_cloud.explorea.data_classes.APIDirectionsDAO;
import com.sonpm_cloud.explorea.data_classes.DirectionsRoute;
import com.sonpm_cloud.explorea.data_classes.Route;
import com.sonpm_cloud.explorea.data_classes.U;
import com.sonpm_cloud.explorea.maps.route_creating.DirectionsCreatingStrategy;

import java.util.List;

public class FragmentMap
        extends Fragment
        implements OnMapReadyCallback {

    private Route route;
    private DirectionsRoute directionsRoute;

    private MapView mapView;
    private GoogleMap googleMap;

    private String getMapViewBundleKey() { return "MapViewBundleKey3"; }
    private int getMapViewId() { return R.id.map_view3; }

//    private FragmentMap() {}

    // Force using non-default constructor
    public FragmentMap() { requireActivity().finish(); }

    public FragmentMap(@NonNull Route route) {
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
                R.layout.activity3_map_fragment,
                container,
                false);
    }

    public void launchMap(APIDirectionsDAO.By what) {
//        LatLng[] directions;
//        if (what == APIDirectionsDAO.By.Foot) directions = PolyUtil.decode(directionsRoute.encodedDirectionsByFoot)
//                .toArray(new LatLng[0]);
//        else if(what == APIDirectionsDAO.By.Bike) directions = PolyUtil.decode(directionsRoute.encodedDirectionsByBike)
//                .toArray(new LatLng[0]);
//        else return;
//        String url = APIDirectionsDAO.createGoogleNavigationURL(directions, what);
        String url = APIDirectionsDAO.createGoogleNavigationURL(directionsRoute.decodedRoute().toArray(new LatLng[0]),
                what);
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

        FragmentMap fragment;
        int padding;

        DirectionsGetTask(FragmentMap fragment,
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
            List<LatLng> decodeDirB = PolyUtil.decode(r.encodedDirectionsByBike);
            PolylineOptions polylineOptionsFoot = new PolylineOptions()
                    .addAll(decodeDirF).color(fragment.requireContext().getColor(R.color.routeFoot));
            PolylineOptions polylineOptionsBike = new PolylineOptions()
                    .addAll(decodeDirB).color(fragment.requireContext().getColor(R.color.routeBike));

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
