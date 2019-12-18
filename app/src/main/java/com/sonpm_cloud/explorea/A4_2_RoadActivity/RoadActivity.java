package com.sonpm_cloud.explorea.A4_2_RoadActivity;

import android.content.Context;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sonpm_cloud.explorea.A2_Login.LoginActivity;
import com.sonpm_cloud.explorea.R;
import com.sonpm_cloud.explorea.data_classes.Route;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RoadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "TAG";
    private String url = "https://explorea-server.azurewebsites.net";

    private RequestQueue requestQueue;

    private long idRoute;
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
    private Button buttonStart;
    private Spinner ratingSpinner;
    private Button buttonRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity4_2_maps);

        Route route = (Route) getIntent().getSerializableExtra("ROUTE");
        idRoute = route.id;
        codedRoute = route.encodedRoute;
        avgRating = route.avgRating;
        lengthByFoot = route.lengthByFoot;
        lengthByBike = route.lengthByBike;
        timeByFoot = route.timeByFoot;
        timeByBike = route.timeByBike;
        city = route.city;


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
        buttonRate.setOnClickListener(v ->
                LoginActivity.silentSignIn(this, this::sendRate, "RoadActivity"));

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentMap fragment = new FragmentMap(route);
        fragmentTransaction.replace(R.id.frameL, fragment).commit();
        findViewById(R.id.buttonStart).setOnClickListener(v ->
                fragment.launchMap());
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
                    Log.d(" RESPONSE JSONPost", "DODANO OCENE");
                    String str = city + " \t " + chosenRate;
                    textViewRoad.setText(str);

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

}
