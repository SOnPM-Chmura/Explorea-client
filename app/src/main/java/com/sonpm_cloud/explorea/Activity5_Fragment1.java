package com.sonpm_cloud.explorea;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class Activity5_Fragment1
        extends AbstractGoogleMapContainerFragment {

    private Activity5_Fragment1_ViewModel viewModel;

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
        viewModel = ViewModelProviders.of(this).get(Activity5_Fragment1_ViewModel.class);

        // TODO: Use the ViewModel
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMinZoomPreference(12);
        LatLng lodz = new LatLng(51.776667, 19.454722);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(lodz));
    }
}
