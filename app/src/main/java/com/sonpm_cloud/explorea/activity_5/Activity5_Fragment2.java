package com.sonpm_cloud.explorea.activity_5;

import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.sonpm_cloud.explorea.Activity4_test;
import com.sonpm_cloud.explorea.data_classes.DirectionsRoute;
import com.sonpm_cloud.explorea.data_classes.Route;
import com.sonpm_cloud.explorea.maps.AbstractGoogleMapContainerFragment;
import com.sonpm_cloud.explorea.R;
import com.sonpm_cloud.explorea.data_classes.MutablePair;
import com.sonpm_cloud.explorea.data_classes.U;
import com.sonpm_cloud.explorea.maps.route_creating.DirectionsCreatingStrategy;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class Activity5_Fragment2 extends AbstractGoogleMapContainerFragment {

    private Polyline lastPolyFoot;
    private Polyline lastPolyBike;

    private long lastCalculation;
    private int lastDistFoot;
    private int lastDistBike;
    private int lastTimeFoot;
    private int lastTimeBike;
    private String lastCity;

    private synchronized void changeParameters(DirectionsRoute route) {
        if (googleMap == null) return;
        if (lastCalculation > route.queryTime) return;
        if (lastPolyFoot != null) {
            lastPolyFoot.remove();
        }
        if (lastPolyBike != null) {
            lastPolyBike.remove();
        }
        PolylineOptions newFoot = new PolylineOptions().addAll(PolyUtil.decode(route.encodedDirectionsByFoot))
                                                       .color(R.color.routeFoot);
        PolylineOptions newBike = new PolylineOptions().addAll(PolyUtil.decode(route.encodedDirectionsByBike))
                                                       .color(R.color.routeBike);
        lastPolyFoot = googleMap.addPolyline(newFoot);
        lastPolyBike = googleMap.addPolyline(newBike);
        lastCalculation = route.queryTime;
        lastDistFoot = route.lengthByFoot;
        lastDistBike = route.lengthByBike;
        lastTimeFoot = route.timeByFoot;
        lastTimeBike = route.timeByBike;
        lastCity = route.city;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(route.bounds,
                                                                 (int) U.dp_px(32, requireContext())));
    }

    private Activity5_Fragment_ViewModel viewModel;

    private GoogleMap googleMap;
    private HashMap<Marker, LatLng> markers;
    private boolean isMapReady = false;

    private RecyclerView recyclerView;
    private Activity5_Fragment_ViewModel.RecyclerAdapter recyclerAdapter;

    public static Activity5_Fragment2 newInstance() { return new Activity5_Fragment2(); }


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.activity5_pointslist_fragment2,
                container,
                false
        );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(Activity5_Fragment_ViewModel.class);

        recyclerView = requireView().findViewById(R.id.recycler_view);

        recyclerAdapter = viewModel.getAdapter();

        ItemTouchHelper.Callback callback = new Activity5_Fragment_ViewModel
                .ItemMoveCallback(recyclerAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(recyclerAdapter);
        requireView().findViewById(R.id.buttonCheck).setOnClickListener(this::sendRoute);
    }

    @Override
    protected int getMapViewId() { return R.id.map_view2; }

    @Override
    protected String getMapViewBundleKey() { return "MapViewBundleKey2"; }

    @Override
    protected void refreshCamera() {  }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.markers = new HashMap<>();
        MutablePair<LatLng, Double> location = fetchLocationCoords();
        LatLng latLng = location.first;
        float zoom = location.second.floatValue();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(latLng, zoom)
        ));

        googleMap.getUiSettings().setAllGesturesEnabled(false);

        googleMap.setOnMarkerClickListener(marker -> true);

        List<MutablePair<LatLng, String>> points = viewModel.getListPoints();

        Marker marker;
        for (MutablePair<LatLng, String> point : points) {
            markers.put(
                    marker = googleMap
                            .addMarker(new MarkerOptions().position(point.first)),
                    point.first);
            marker.setDraggable(true);
        }

        viewModel.getPoints().observe(this, _points -> {

            if (_points.size() > 1 && _points.size() < 25)
                new DirectionsGetTask(this).execute(StreamSupport.stream(viewModel.getListPoints())
                                                                                        .map(p -> p.first)
                                                                                        .toArray(LatLng[]::new));

            List<LatLng> toRemove = new LinkedList<>(markers.values());
            List<LatLng> toAdd = StreamSupport.stream(_points).map(p -> p.first)
                    .collect(Collectors.toList());

            List<LatLng> temp = new LinkedList<>(toRemove);

            toRemove.removeAll(toAdd);
            toAdd.removeAll(temp);

            if (toRemove.size() > 0) {
                List<Marker> removing = StreamSupport.stream(markers.entrySet())
                        .filter(m -> toRemove.contains(m.getValue()))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());

                StreamSupport.stream(removing).forEach(m -> {
                    m.remove();
                    markers.remove(m);
                });
            }

            if (toAdd.size() > 0) {
                StreamSupport.stream(toAdd).forEach(m -> {
                    Marker mm = googleMap.addMarker(new MarkerOptions().position(m));
                    markers.put(mm, m);
                });
            }
        });
        isMapReady = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.googleMap != null && isMapReady) {
            LatLng[] markers = StreamSupport.stream(this.markers.entrySet())
                    .map(Map.Entry::getValue)
                    .toArray(LatLng[]::new);
            if (this.markers.size() > 1) {
                float dp = 32;
                LatLngBounds.Builder bounds = LatLngBounds.builder();
                for (LatLng marker : markers) bounds.include(marker);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(),
                        (int) U.dp_px(dp, requireContext())));
                int viewHeight = (int) U.dp_px(U.px_dp(
                        mapView.getHeight(), requireContext())/2 - dp/2, requireContext());
                int viewWidth = mapView.getWidth()/2;
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(
                        googleMap.getProjection()
                                .fromScreenLocation(new Point(viewWidth, viewHeight))));
            } else if (this.markers.size() == 1) {
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(markers[0], getDefZoom())));
            }
        }
    }

    private void sendRoute(View view) {
        if (lastDistFoot > 10000) {
            Toast.makeText(requireContext(),
                           getString(R.string.distFootTooLong)
                                   .replaceAll("\\%1", String.valueOf(lastDistFoot))
                                   .replaceAll("\\%2", "10000"),
                           Toast.LENGTH_LONG).show();
            return;
        }
        if (lastDistBike > 30000) {
            Toast.makeText(requireContext(),
                           getString(R.string.distBikeTooLong)
                                   .replaceAll("\\%1", String.valueOf(lastDistBike))
                                   .replaceAll("\\%2", "10000"),
                           Toast.LENGTH_LONG).show();
            return;
        }
        if (lastPolyFoot.getPoints().size() > 25) {
            Toast.makeText(requireContext(),
                           getString(R.string.pointCountTooBig)
                                   .replaceAll("\\%1", String.valueOf(lastPolyFoot.getPoints().size()))
                                   .replaceAll("\\%2", "25"),
                           Toast.LENGTH_LONG).show();
            return;
        }
        List<LatLng> lll = StreamSupport.stream(viewModel.getListPoints())
                .map(p -> p.first)
                .collect(Collectors.toList());
        Route ret = new Route(-1,
                lll,
                0f,
                lastDistFoot,
                lastDistBike,
                lastTimeFoot,
                lastTimeBike,
                lastCity);

        Log.e("sendRoute", ret.toString());
        Intent intent = new Intent(requireContext(), Activity4_test.class);
        intent.putExtra("ROUTE", ret);
        startActivity(intent);
    }

    private static class DirectionsGetTask
            extends AsyncTask<LatLng, Void, DirectionsRoute> {

        Activity5_Fragment2 fragment;

        DirectionsGetTask(Activity5_Fragment2 fragment) {
            this.fragment = fragment;
        }

        @Override
        protected DirectionsRoute doInBackground(LatLng... latLngs) {

            return DirectionsCreatingStrategy
                    .getRecommendedStrategy(latLngs, fragment.requireContext())
                    .createDirectionsRoute();
        }

        @Override
        protected void onPostExecute(DirectionsRoute result) {

            if (result == null) {
                Toast.makeText(fragment.requireContext(),
                               fragment.getString(R.string.directions_null),
                               Toast.LENGTH_LONG).show();
                return;
            }
            fragment.changeParameters(result);
        }
    }
}
