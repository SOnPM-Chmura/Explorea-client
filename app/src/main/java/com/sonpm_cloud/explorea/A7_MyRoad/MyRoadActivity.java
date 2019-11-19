package com.sonpm_cloud.explorea.A7_MyRoad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sonpm_cloud.explorea.Model.Route;
import com.sonpm_cloud.explorea.R;
import com.sonpm_cloud.explorea.A4_2_RoadActivity.RoadActivity;

public class MyRoadActivity extends AppCompatActivity {

    private static final String TAG = "@@@@@@";//MainActivity.class.getCanonicalName();
    private String url = "https://explorea-server.azurewebsites.net";
    private RequestQueue requestQueue;
    private LinearLayout linearLayoutForRoads;

    private int idRoute;
    private String codedRoute;
    private double avgRating;
    private int lengthByFoot;
    private int lengthByBike;
    private int timeByFoot;
    private int timeByBike;
    private String city;

    private String[] createdRoutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity7_myroad);

        createdRoutes = getIntent().getStringArrayExtra("createdRoutes");

        linearLayoutForRoads = findViewById(R.id.RoadButtonList);
        requestQueue =  Volley.newRequestQueue(this);

        sendRequest();
    }

    private void sendRequest() {
        Context context = this;
        for (String routeId : createdRoutes){
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url + "/routes/"+routeId,
                    null,
                    response -> {
                        try {
                            idRoute = response.getInt("id");
                            codedRoute = response.getString("codedRoute");
                            avgRating = (!response.get("avgRating").toString().equals("null")) ?  response.getDouble("avgRating") : 0;
                            lengthByFoot = response.getInt("lengthByFoot");
                            lengthByBike = response.getInt("lengthByBike");
                            timeByFoot = response.getInt("timeByFoot");
                            timeByBike = response.getInt("timeByBike");
                            city = (response.getString("city") != null) ? response.getString("city"): "";

                            Route route = new Route(idRoute,codedRoute,avgRating,lengthByFoot,lengthByBike,timeByFoot,timeByBike,city);
                            Button btnShow = new Button(this);
                            String str = city + " \tOcena: " + avgRating + "\nBy foot: " + lengthByFoot + " m, " + timeByFoot + " min" + "\nBy bike: " + lengthByBike + " m, " + timeByBike + " min";
                            btnShow.setText(str);
                            btnShow.setId(idRoute);
                            btnShow.setAllCaps(false);
                            btnShow.setLines(3);
                            btnShow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                            btnShow.setOnClickListener(v -> startActivity(new Intent(v.getContext(), RoadActivity.class)));
                            btnShow.setOnClickListener(v -> {
                                Intent intent = new Intent(v.getContext(), RoadActivity.class);
                                intent.putExtra("idRoute", route.getId());
                                intent.putExtra("codedRoute", route.getCodedRoute());
                                intent.putExtra("avgRating", route.getAverageRating());
                                intent.putExtra("lengthByFoot", route.getLengthByFoot());
                                intent.putExtra("lengthByBike", route.getLengthByBike());
                                intent.putExtra("timeByFoot", route.getTimeByFoot());
                                intent.putExtra("timeByBike", route.getTimeByBike());
                                intent.putExtra("city", route.getCity());
                                startActivity(intent);
                            });

                            // Add Button to LinearLayout
                            if (linearLayoutForRoads != null) {
//                                    Log.d("@", "HERE");
                                linearLayoutForRoads.addView(btnShow);
                            }

                        }catch (Exception e) {
                            e.printStackTrace();
                            Log.w(TAG, "request response:failed message=" + e.getMessage());
                        }
                    },
                    error -> {
                        Toast.makeText(context, getString(R.string.request_error_response_msg), Toast.LENGTH_LONG)
                                .show();
                        Log.w(TAG, "request response:failed time=" + error.getNetworkTimeMs());
                        Log.w(TAG, "request response:failed msg=" + error.getMessage());
                    }
            );
            requestQueue.add(jsonObjectRequest);
        }
    }
}
