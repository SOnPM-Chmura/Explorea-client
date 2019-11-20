package com.sonpm_cloud.explorea.activity_5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sonpm_cloud.explorea.maps.AbstractGoogleMapContainerFragment;
import com.sonpm_cloud.explorea.R;
import com.sonpm_cloud.explorea.data_classes.MutablePair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class Activity5_Fragment1
        extends AbstractGoogleMapContainerFragment {

    private Activity5_Fragment_ViewModel viewModel;

    private GoogleMap googleMap;
    private HashMap<Marker, LatLng> markers;

    @Override
    protected int getMapViewId() { return R.id.map_view; }

    @Override
    protected String getMapViewBundleKey() { return "MapViewBundleKey"; }

    public static Activity5_Fragment1 newInstance() { return new Activity5_Fragment1(); }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.activity5_markingpoints_fragment1,
                container,
                false
        );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(Activity5_Fragment_ViewModel.class);
    }

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

        googleMap.setOnMarkerClickListener(marker -> true);

        googleMap.setOnPoiClickListener(pointOfInterest -> viewModel.addPoint(
                MutablePair.create(pointOfInterest.latLng, pointOfInterest.name)));

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                marker.remove();
                viewModel.removePoint(markers.get(marker));
            }

            @Override
            public void onMarkerDrag(Marker marker) {  }

            @Override
            public void onMarkerDragEnd(Marker marker) {  }
        });

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
                    mm.setDraggable(true);
                    markers.put(mm, m);
                });
            }
        });
    }

    @Override
    public void refreshCamera() {
        MutablePair<LatLng, Double> location = fetchLocationCoords();
        LatLng latLng = location.first;
        float zoom = location.second.floatValue();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(latLng, zoom)
        ));
    }
}
