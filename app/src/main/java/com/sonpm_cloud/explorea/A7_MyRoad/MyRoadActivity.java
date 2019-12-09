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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.sonpm_cloud.explorea.A4_2_RoadActivity.RoadActivity;
import com.sonpm_cloud.explorea.Model.RouteModel;
import com.sonpm_cloud.explorea.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
    private String token;

//    private String[] createdRoutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity7_myroad);

//        createdRoutes = getIntent().getStringArrayExtra("createdRoutes");
        token = getIntent().getStringExtra("token");

        linearLayoutForRoads = findViewById(R.id.RoadButtonList);
        requestQueue =  Volley.newRequestQueue(this);

        sendRequest();
    }

    private void sendRequest() {
        Context context = this;
//        for (String routeId : createdRoutes){
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    url + "/routes/created",
                    null,
                    response -> {
                        try {
                            if (response == null) {
                                Toast.makeText(context, getString(R.string.empty_creaded_route_msg), Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        JSONObject jsonObject = (JSONObject) response.get(i);
                                        Log.d("jsonObject", response.get(i).toString());
                                        idRoute = jsonObject.getInt("id");
                                        codedRoute = jsonObject.getString("codedRoute");
                                        avgRating = (!jsonObject.get("avgRating").toString().equals("null")) ? jsonObject.getDouble("avgRating") : 0; //avgRating = jsonObject.getDouble("avgRating");
                                        lengthByFoot = jsonObject.getInt("lengthByFoot");
                                        lengthByBike = jsonObject.getInt("lengthByBike");
                                        timeByFoot = jsonObject.getInt("timeByFoot");
                                        timeByBike = jsonObject.getInt("timeByBike");
                                        city = (jsonObject.getString("city") != null) ? jsonObject.getString("city") : "";

                                        RouteModel route = new RouteModel(idRoute, codedRoute, avgRating, lengthByFoot, lengthByBike, timeByFoot, timeByBike, city);
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

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.w(TAG, "request response:failed message=" + e.getMessage());
                                    }
                                }
                            }
//                        Log.d("@@@2", idRoute + " " + codedRoute);

                        } catch (Exception e) {
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
            ) {
                /** Passing some request headers* */
                @Override
                public Map getHeaders() {
                    HashMap headers = new HashMap();
                    headers.put("authorization", "Bearer " + token);
                    return headers;
                }
            };
            requestQueue.add(jsonObjectRequest);
//        }
    }
}
