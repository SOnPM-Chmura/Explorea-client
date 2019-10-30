package com.sonpm_cloud.explorea;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Pair;

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
    MapView mapView;
    LocationManager locationManager;
    FusedLocationProviderClient fusedLocationClient;

    @FunctionalInterface
    private interface LocationGetter { Pair<LatLng, Double> get();}
    LocationGetter currentLocationGetter = this::getDefaultLocation;

    abstract int getMapViewId();
    abstract String getMapViewBundleKey();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null)
            mapViewBundle = savedInstanceState.getBundle(getMapViewBundleKey());


        if (
            ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, REQUEST_LOCATION);
        } else {
            currentLocationGetter = this::getCurrentLocation;
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        mapView = getView().findViewById(getMapViewId());
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

    public Pair<LatLng, Double> fetchLocationCoords() {
        return currentLocationGetter.get();
    }

    private Pair<LatLng, Double> getCurrentLocation() {

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

        return Pair.create(result, 14.0);
    }

    private Pair<LatLng, Double> getDefaultLocation() {
        return Pair.create(new LatLng(51.925, 19.13075), 5.6);
    }

    abstract void refreshCamera();
}
