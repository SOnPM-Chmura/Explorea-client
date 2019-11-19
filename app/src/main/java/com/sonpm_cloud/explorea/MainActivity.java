package com.sonpm_cloud.explorea;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.sonpm_cloud.explorea.A2_Login.LoginActivity;
import com.sonpm_cloud.explorea.A4_2_RoadActivity.RoadActivity;
import com.sonpm_cloud.explorea.A4_SearchRoad.SearchRoadActivity;
import com.sonpm_cloud.explorea.A5_CreateRoad.CreateRoadActivity;
import com.sonpm_cloud.explorea.A6_FavouriteRoad.FavouriteRoadActivity;
import com.sonpm_cloud.explorea.A7_MyRoad.MyRoadActivity;
import com.sonpm_cloud.explorea.Model.Route;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "@@@@@@";//MainActivity.class.getCanonicalName();
    private String url = "https://explorea-server.azurewebsites.net";
    private RequestQueue requestQueue;
    private LinearLayout linearLayoutForRoads;// = findViewById(R.id.RoadButtonList);
    private List<Route> routes;

    private int idRoute;
    private String codedRoute;
    private double avgRating;
    private int lengthByFoot;
    private int lengthByBike;
    private int timeByFoot;
    private int timeByBike;
    private String city;

    private String[] createdRoutes;
    private String[] favoriteRoutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity3_main_roadlist);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LoginActivity.account = GoogleSignIn.getLastSignedInAccount(this);
        if (LoginActivity.account == null) {
            launchLoginActivity();
        }
        linearLayoutForRoads = findViewById(R.id.RoadButtonList);
        requestQueue =  Volley.newRequestQueue(this);//VolleySingleton.getInstance(this).getRequestQueue();

        routes = new ArrayList<>();

        sendRequest();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean result = false;

        switch (id) {
            case R.id.search_track_button:
                startActivity(new Intent(this, SearchRoadActivity.class));
                result = true;
                break;
            case R.id.create_track_button:
                startActivity(new Intent(this, CreateRoadActivity.class));
                result = true;
                break;
            case R.id.favourite_track_button:
                sendRequestForUsers("4", "FavouriteRoadActivity");
                result = true;
                break;
            case R.id.mine_track_button:
                sendRequestForUsers("5", "MyRoadActivity");
                result = true;
                break;
            case R.id.logout_button:
                signOut();
                result = true;
                break;
        }
        if (result)
            return true;

        return super.onOptionsItemSelected(item);
    }

    private void launchLoginActivity() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void signOut() {
        if (LoginActivity.mGoogleSignInClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail().build();
            LoginActivity.mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        }

        LoginActivity.mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            LoginActivity.account = null;
            launchLoginActivity();
        });
    }

    private void sendRequest() {
        Context context = this;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url + "/routes",
                null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = (JSONObject) response.get(i);
                                Log.d("jsonObject", response.get(i).toString());
                                idRoute = jsonObject.getInt("id");
                                codedRoute = jsonObject.getString("codedRoute");
                                avgRating = (!jsonObject.get("avgRating").toString().equals("null")) ?  jsonObject.getDouble("avgRating") : 0; //avgRating = jsonObject.getDouble("avgRating");
                                lengthByFoot = jsonObject.getInt("lengthByFoot");
                                lengthByBike = jsonObject.getInt("lengthByBike");
                                timeByFoot = jsonObject.getInt("timeByFoot");
                                timeByBike = jsonObject.getInt("timeByBike");
                                city = (jsonObject.getString("city") != null) ? jsonObject.getString("city"): "";

                                Route route = new Route(idRoute,codedRoute,avgRating,lengthByFoot,lengthByBike,timeByFoot,timeByBike,city);
//                                routes.add(new Route(idRoute,codedRoute,avgRating,lengthByFoot,lengthByBike,timeByFoot,timeByBike,city));
                                Button btnShow = new Button(this);
                                String str = city + " \tOcena: " + avgRating + "\nBy foot: " + lengthByFoot + " m, " + timeByFoot + " min" + "\nBy bike: " + lengthByBike + " m, " + timeByBike + " min";
                                btnShow.setText(str);
                                btnShow.setId(idRoute);
                                btnShow.setAllCaps(false);
                                btnShow.setLines(3);
                                btnShow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                                btnShow.setOnClickListener(v -> startActivity(new Intent(v.getContext(), RoadActivity.class)));
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

                            } catch (JSONException e) {
                                e.printStackTrace();
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
        );
        requestQueue.add(jsonArrayRequest);
    }

    private void sendRequestForUsers(String userId, String activityName){
        Context context = this;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url + "/users/" + userId,
                null,
                response -> {
                    try {

                        Log.d("jsonObject", response.toString());
                        createdRoutes = response.getString("createdRoutes").split("_");
                        favoriteRoutes = response.getString("favoriteRoutes").split("_");
                        Log.d("EXTRA createdRoutes", Arrays.toString(createdRoutes));
                        Log.d("EXTRA favoriteRoutes", Arrays.toString(favoriteRoutes));

                        if (activityName.equals("MyRoadActivity")){
                            Intent intent = new Intent(this, MyRoadActivity.class);
                            intent.putExtra("createdRoutes", createdRoutes);
                            startActivity(intent);
                        } else if (activityName.equals("FavouriteRoadActivity")){
                            Intent intent = new Intent(this, FavouriteRoadActivity.class);
                            intent.putExtra("favoriteRoutes", favoriteRoutes);
                            startActivity(intent);
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


/////////////////////////////////////////////
//    private void setButtonsForRoads(){

//        sendRequest();
//        try {
//            wait(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Thread.currentThread().getName();
//        Thread.activeCount()

//        int sleepTime = 200;
//        int counter = 0;
//        int timeout = 15000;
//        while(routes.size() == 0){
//            try {
//                if(counter > timeout){
//                    throw new Exception("Exception with response");
//                }
//                else {
//                    counter += sleepTime;
//                    Thread.sleep(sleepTime);
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }


//        for (int i = 0; i < 5; i++) {
//            Button btnShow = new Button(this);
//            btnShow.setText("TRASA");
//            btnShow.setId(i);
//            btnShow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            btnShow.setOnClickListener(v -> startActivity(new Intent(v.getContext(), RoadActivity.class)));
//
//            // Add Button to LinearLayout
//            if (linearLayoutForRoads != null) {
//                Log.d("@", "HERE");
//                linearLayoutForRoads.addView(btnShow);
//            }
//        }
//    }


