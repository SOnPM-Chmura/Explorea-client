package com.sonpm_cloud.explorea;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class Activity5_Fragment1
        extends AbstractGoogleMapContainerFragment {

    Activity5_Fragment_ViewModel viewModel;

    private GoogleMap googleMap;


    @Override
    int getMapViewId() { return R.id.map_view; }

    @Override
    String getMapViewBundleKey() { return "MapViewBundleKey"; }

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
        viewModel = ViewModelProviders.of(getActivity()).get(Activity5_Fragment_ViewModel.class);
        System.out.println(viewModel.getPoints());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        Pair<LatLng, Double> location = fetchLocationCoords();
        googleMap.setMinZoomPreference(location.second.floatValue());
        LatLng latLng = location.first;
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        googleMap.setOnPoiClickListener(pointOfInterest -> {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(pointOfInterest.latLng);
            Marker marker = googleMap.addMarker(markerOptions);
            marker.setDraggable(true);

            viewModel.addPoint(Pair.create(marker, " todo"));
        });

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                marker.remove();
                viewModel.removePoint(marker);
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }
        });

        List<Pair<Marker, String>> points = viewModel.getPoints();
        viewModel.clearPoints();

        for (Pair<Marker, String> point : points) {
            viewModel.addPoint(Pair.create(
                    googleMap.addMarker(new MarkerOptions().position(point.first.getPosition())),
                    point.second));
        }
    }

    @Override
    public void refreshCamera() {
        Pair<LatLng, Double> location = fetchLocationCoords();
        googleMap.setMinZoomPreference(location.second.floatValue());
        LatLng latLng = location.first;
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
}
