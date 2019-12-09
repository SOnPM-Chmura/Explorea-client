package com.sonpm_cloud.explorea.A4_2_RoadActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.sonpm_cloud.explorea.A2_Login.LoginActivity;
import com.sonpm_cloud.explorea.R;
import com.sonpm_cloud.explorea.data_classes.MutablePair;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RoadActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener{

    private static final String TAG = "@@@@@@";//MainActivity.class.getCanonicalName();
    private String url = "https://explorea-server.azurewebsites.net";

    private static final int REQUEST_LOCATION = 420;
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationClient;
    private interface LocationGetter { MutablePair<LatLng, Double> get();}
    private LocationGetter currentLocationGetter = this::getDefaultLocation;
    private GoogleMap googleMap;
    private RequestQueue requestQueue;

    private int idRoute;
    private String codedRoute;
    private double avgRating;
    private int lengthByFoot;
    private int lengthByBike;
    private int timeByFoot;
    private int timeByBike;
    private String city;
    private Integer[] rate = {5, 4, 3, 2, 1};
    private int chosenRate;

    private TextView textViewRoad;
    private TextView textViewRoadInfo;
    private MapView mapView;
    private Button buttonStart;
    private Spinner ratingSpinner;
    private Button buttonRate;

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

        ratingSpinner = findViewById(R.id.ratingSpinner);

        ArrayAdapter<Integer> arrayRate = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, rate);
        arrayRate.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        ratingSpinner.setAdapter(arrayRate);

        ratingSpinner.setOnItemSelectedListener(this);

        buttonRate = findViewById(R.id.buttonRate);

        requestQueue =  Volley.newRequestQueue(this);
        buttonRate.setOnClickListener(v -> sendRate());

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(String.valueOf(parent).contains("ratingSpinner")) {
            chosenRate = rate[position];
            System.out.println(chosenRate);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if(String.valueOf(parent).contains("ratingSpinner")) {
            chosenRate = 0;
        }
    }

    private void sendRate(){
        Context context = this;
        Map<String, String> params = new HashMap<>();
        params.put("routeId", String.valueOf(idRoute));
        params.put("rating", String.valueOf(chosenRate));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,
                url + "/ratings",
                new JSONObject(params),
                response -> {
//                    Log.d(" RESPONSE JSONPost", response.toString());
                    Log.d(" RESPONSE JSONPost", "DODANO OCENE ?");
                },
                error -> {
                    Toast.makeText(context, getString(R.string.request_error_response_msg), Toast.LENGTH_LONG)
                            .show();
                    Log.w(TAG, "request response:failed time=" + error.getNetworkTimeMs());
                    Log.w(TAG, "request response:failed msg=" + error.getMessage());
                }
        ) {
            /** Passing some request headers* */
            @Override
            public Map getHeaders() {
                HashMap headers = new HashMap();
                headers.put("authorization", "Bearer " + LoginActivity.account.getIdToken());
                return headers;
            }
        };
        requestQueue.add(jsonObjReq);
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
