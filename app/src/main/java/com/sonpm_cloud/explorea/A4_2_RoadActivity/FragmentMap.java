package com.sonpm_cloud.explorea.A4_2_RoadActivity;

import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.Polyline;
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

    private Polyline polylineFoot;
    private Polyline polylineBike;

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
        View inflate = inflater.inflate(
                R.layout.activity3_map_fragment,
                container,
                false);

        inflate.findViewById(R.id.walk_toggle).setOnClickListener(this::routeToggleHandler);
        inflate.findViewById(R.id.bike_toggle).setOnClickListener(this::routeToggleHandler);

        return inflate;
    }

    public void launchMap() {
        boolean isFoot = polylineFoot.isVisible();
        boolean isBike = polylineBike.isVisible();
        APIDirectionsDAO.By whatParam = null;
        if (isFoot && !isBike) whatParam = APIDirectionsDAO.By.Foot;
        if (!isFoot && isBike) whatParam = APIDirectionsDAO.By.Bike;
        GoogleNavigationLaunchDialog.newInstance(whatParam, route.encodedRoute)
                                    .show(requireActivity().getSupportFragmentManager(), "NAV_LAUNCHER");
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
            fragment.polylineFoot = fragment.googleMap.addPolyline(result.first.first);
            fragment.polylineBike = fragment.googleMap.addPolyline(result.first.second);
            fragment.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(result.second, padding));
        }
    }

    private void routeToggleHandler(View view) {

        boolean isFoot = polylineFoot.isVisible();
        boolean isBike = polylineBike.isVisible();

        final ColorStateList TINT_DISABLED = ColorStateList.valueOf(requireContext().getColor(android.R.color.darker_gray));
        final ColorStateList TINT_FOOT = ColorStateList.valueOf(requireContext().getColor(R.color.routeFoot));
        final ColorStateList TINT_BIKE = ColorStateList.valueOf(requireContext().getColor(R.color.routeBike));

        ImageView footToogle = requireView().findViewById(R.id.walk_toggle);
        ImageView bikeToogle = requireView().findViewById(R.id.bike_toggle);

        switch (view.getId()) {
            case R.id.walk_toggle:
                if (!isFoot) {
                    polylineFoot.setVisible(true);
                    footToogle.setImageTintList(TINT_FOOT);
                } else if (isFoot && !isBike) {
                    polylineFoot.setVisible(false);
                    footToogle.setImageTintList(TINT_DISABLED);
                    polylineBike.setVisible(true);
                    bikeToogle.setImageTintList(TINT_BIKE);
                } else if (isFoot && isBike) {
                    polylineFoot.setVisible(false);
                    footToogle.setImageTintList(TINT_DISABLED);
                }
                break;
            case R.id.bike_toggle:
                if (!isBike) {
                    polylineBike.setVisible(true);
                    bikeToogle.setImageTintList(TINT_BIKE);
                } else if (isBike && !isFoot) {
                    polylineBike.setVisible(false);
                    bikeToogle.setImageTintList(TINT_DISABLED);
                    polylineFoot.setVisible(true);
                    footToogle.setImageTintList(TINT_FOOT);
                } else if (isBike && isFoot) {
                    polylineBike.setVisible(false);
                    bikeToogle.setImageTintList(TINT_DISABLED);
                }
                break;
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
