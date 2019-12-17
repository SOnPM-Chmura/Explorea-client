package com.sonpm_cloud.explorea.maps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.sonpm_cloud.explorea.data_classes.MutablePair;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractGoogleMapContainerFragment
        extends Fragment
        implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_LOCATION = 420;
    protected MapView mapView;
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationClient;

    @FunctionalInterface
    private interface LocationGetter { MutablePair<LatLng, Double> get();}
    private LocationGetter currentLocationGetter = this::getDefaultLocation;

    protected abstract int getMapViewId();
    protected abstract String getMapViewBundleKey();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null)
            mapViewBundle = savedInstanceState.getBundle(getMapViewBundleKey());


        if (
                ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, REQUEST_LOCATION);
        } else {
            currentLocationGetter = this::getCurrentLocation;
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        mapView = requireView().findViewById(getMapViewId());
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (permissions.length == 0) currentLocationGetter = this::getDefaultLocation;
        if (grantResults.length == 0) currentLocationGetter = this::getDefaultLocation;
        int resultSum = 0;
        for (int grantResult : grantResults) resultSum += grantResult;

        if (resultSum != 0) currentLocationGetter = this::getDefaultLocation;
        else {
            currentLocationGetter = this::getCurrentLocation;
            refreshCamera();
        }
    }

    protected MutablePair<LatLng, Double> fetchLocationCoords() { return currentLocationGetter.get(); }

    protected static float getDefZoom() { return 14.0f; }

    private MutablePair<LatLng, Double> getCurrentLocation() {

        Task<Location> locationTask = fusedLocationClient.getLastLocation();

        Future<LatLng> future = Executors.newSingleThreadExecutor().submit(() -> {
            double latitude;
            double longitude;
            Location result;
            try {
                result = Tasks.await(locationTask, 500, TimeUnit.MILLISECONDS);
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                e.printStackTrace();
                return null;
            }
            if (result == null) return null;
            latitude = result.getLatitude();
            longitude = result.getLongitude();

            return new LatLng(latitude, longitude);
        });
        LatLng result = null;

        try {
            result = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (result == null) return getDefaultLocation();

        return MutablePair.create(result, (double) getDefZoom());
    }

    private MutablePair<LatLng, Double> getDefaultLocation() {
        return MutablePair.create(new LatLng(51.925, 19.13075), 5.6);
    }

    protected abstract void refreshCamera();
}