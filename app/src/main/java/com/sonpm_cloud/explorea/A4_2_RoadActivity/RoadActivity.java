package com.sonpm_cloud.explorea.A4_2_RoadActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.sonpm_cloud.explorea.R;
import com.sonpm_cloud.explorea.data_classes.MutablePair;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RoadActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION = 420;
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationClient;
    private interface LocationGetter { MutablePair<LatLng, Double> get();}
    private LocationGetter currentLocationGetter = this::getDefaultLocation;
    private GoogleMap googleMap;

    private int idRoute;
    private String codedRoute;
    private double avgRating;
    private int lengthByFoot;
    private int lengthByBike;
    private int timeByFoot;
    private int timeByBike;
    private String city;

    private TextView textViewRoad;
    private TextView textViewRoadInfo;
    private MapView mapView;
    private Button buttonStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity4_2_maps);

        idRoute = getIntent().getIntExtra("idRoute", -1);
        codedRoute = getIntent().getStringExtra("codedRoute");
        avgRating = getIntent().getDoubleExtra("avgRating", -1);
        lengthByFoot = getIntent().getIntExtra("lengthByFoot", -1);
        lengthByBike = getIntent().getIntExtra("lengthByBike", -1);
        timeByFoot = getIntent().getIntExtra("timeByFoot", -1);
        timeByBike = getIntent().getIntExtra("timeByBike", -1);
        city = getIntent().getStringExtra("city");

        textViewRoad = findViewById(R.id.textViewRoad);
        String str = city + " \t " + avgRating;
        textViewRoad.setText(str);

        textViewRoadInfo = findViewById(R.id.textViewRoadInfo);
        str = "By foot: " + lengthByFoot + " m, " + timeByFoot + " min" + "\nBy bike: " + lengthByBike + " m, " + timeByBike + " min";
        textViewRoadInfo.setText(str);

        buttonStart = findViewById(R.id.buttonStart); // add onClick -> go to Google with route


        //// MAPA ////
        Bundle mapViewBundle = null;
        if (savedInstanceState != null)
            mapViewBundle = savedInstanceState.getBundle("MapViewBundleKey");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_LOCATION);
        } else {
            currentLocationGetter = this::getCurrentLocation;
        }
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mapView = findViewById(R.id.map_view4_2);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        //// MAPA ////

    }

    //// MAPA ////
    private MutablePair<LatLng, Double> getDefaultLocation() {
        return MutablePair.create(new LatLng(51.925, 19.13075), 5.6);
    }

    //// MAPA ////
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

        return MutablePair.create(result, (double) 14.0f);
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
    public void onMapReady(GoogleMap googleMap) {
//        MutablePair<LatLng, Double> location = fetchLocationCoords();
//        LatLng latLng = location.first;
//        float zoom = location.second.floatValue();
//        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(
//        CameraPosition.fromLatLngZoom(latLng, zoom)
//        ));
    }

    //// MAPA ////
    protected MutablePair<LatLng, Double> fetchLocationCoords() { return currentLocationGetter.get(); }
}
