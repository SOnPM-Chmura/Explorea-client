package com.sonpm_cloud.explorea;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.sonpm_cloud.explorea.A2_Login.LoginActivity;
import com.sonpm_cloud.explorea.A4_2_RoadActivity.RoadActivity;
import com.sonpm_cloud.explorea.A4_SearchRoad.SearchRoadActivity;
import com.sonpm_cloud.explorea.A5_CreateRoad.CreateRoadActivity;
import com.sonpm_cloud.explorea.A6_FavouriteRoad.FavouriteRoadActivity;
import com.sonpm_cloud.explorea.A7_MyRoad.MyRoadActivity;
import com.sonpm_cloud.explorea.data_classes.Route;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "@@@@@@";//MainActivity.class.getCanonicalName();
    private String url = "https://explorea-server.azurewebsites.net";
    private RequestQueue requestQueue;
    private LinearLayout linearLayoutForRoads;
    private boolean connected;

    private int idRoute;
    private String codedRoute;
    private double avgRating;
    private int lengthByFoot;
    private int lengthByBike;
    private int timeByFoot;
    private int timeByBike;
    private String city;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity3_main_roadlist);

        connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        connected = connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable()
                && connectivityManager.getActiveNetworkInfo().isConnected();

        if (connected) {
            LoginActivity.account = GoogleSignIn.getLastSignedInAccount(this);
            if (LoginActivity.account == null) {
                launchLoginActivity();
            }

            Log.d("TOKEN ", LoginActivity.account.getIdToken());

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            linearLayoutForRoads = findViewById(R.id.RoadButtonList);
            requestQueue =  Volley.newRequestQueue(this);

            LoginActivity.silentSignIn(this, this::sendAddUser, "MainActivity");
        }
        else {
            Toast.makeText(this, getString(R.string.no_network_connection), Toast.LENGTH_LONG)
                    .show();
        }
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
                startActivity(new Intent(this, FavouriteRoadActivity.class));
                result = true;
                break;
            case R.id.mine_track_button:
                startActivity(new Intent(this, MyRoadActivity.class));
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

    private void sendGetRoutes() {
        Context context = this;
        linearLayoutForRoads.removeAllViews();
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
                                codedRoute = Route.hexDecode(jsonObject.getString("codedRoute"));
                                avgRating = (!jsonObject.get("avgRating").toString().equals("null")) ?  jsonObject.getDouble("avgRating") : 0; //avgRating = jsonObject.getDouble("avgRating");
                                lengthByFoot = jsonObject.getInt("lengthByFoot");
                                lengthByBike = jsonObject.getInt("lengthByBike");
                                timeByFoot = jsonObject.getInt("timeByFoot");
                                timeByBike = jsonObject.getInt("timeByBike");
                                city = (jsonObject.getString("city") != null) ? jsonObject.getString("city"): "";

                                Route route = new Route(idRoute,codedRoute,(float)avgRating,lengthByFoot,lengthByBike,timeByFoot,timeByBike,city);
                                Button btnShow = new Button(this);
                                String str = city + " \tOcena: " + avgRating + "\nBy foot: " + lengthByFoot + " m, " + timeByFoot + " min" + "\nBy bike: " + lengthByBike + " m, " + timeByBike + " min";
                                btnShow.setText(str);
                                btnShow.setId(idRoute);
                                btnShow.setAllCaps(false);
                                btnShow.setLines(3);
                                btnShow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                                btnShow.setOnClickListener(v -> {
                                    Intent intent = new Intent(v.getContext(), RoadActivity.class);
                                    intent.putExtra("ROUTE", route);
                                    startActivity(intent);
                                });

                                // Add Button to LinearLayout
                                if (linearLayoutForRoads != null) {
                                    linearLayoutForRoads.addView(btnShow);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

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
                headers.put("authorization", "Bearer " + LoginActivity.account.getIdToken());
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    private void sendAddUser(){
        Context context = this;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,
                url + "/users",
                null,
                response -> {
                    if (response != null)
                        Log.d(" RESPONSE JSONPost", response.toString());
//                    Log.d(" RESPONSE JSONPost", "DODANO");
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

    @Override
    protected void onStart() {
        super.onStart();
        Context context = this;
        if (connected) {
            //we are connected to a network
            LoginActivity.silentSignIn(this, this::sendGetRoutes, "MainActivity");
        }
        else
            Toast.makeText(context, getString(R.string.no_network_connection), Toast.LENGTH_LONG)
                    .show();
    }
}

//        StringRequest postRequest = new StringRequest(
//                Request.Method.POST,
//                url + "/users",
//                response -> {
//                    Log.d("RESPONSE", response);
//                },
//                error -> {
//                    Toast.makeText(context, getString(R.string.request_error_response_msg), Toast.LENGTH_LONG)
//                            .show();
//                    Log.w(TAG, "request response:failed time=" + error.getNetworkTimeMs());
//                    Log.w(TAG, "request response:failed msg=" + error.getMessage());
//                }
//        ) {
//            /** Passing some request headers* */
//            @Override
//            public Map getHeaders() {
//                HashMap headers = new HashMap();
//                headers.put("authorization", "Bearer " + token);
//                return headers;
//            }
//        };
//        requestQueue.add(postRequest);

//    private void sendRequestForUsers(String userId, String activityName){
//        Context context = this;
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                Request.Method.GET,
//                url + "/users/" + userId,
//                null,
//                response -> {
//                    try {
//
//                        Log.d("jsonObject", response.toString());
//                        createdRoutes = response.getString("createdRoutes").split("_");
//                        favoriteRoutes = response.getString("favoriteRoutes").split("_");
//                        Log.d("EXTRA createdRoutes", Arrays.toString(createdRoutes));
//                        Log.d("EXTRA favoriteRoutes", Arrays.toString(favoriteRoutes));
//
//                        Log.d("TOKEN ", token);
//                        if (activityName.equals("MyRoadActivity")){
//                            Intent intent = new Intent(this, MyRoadActivity.class);
//                            intent.putExtra("createdRoutes", createdRoutes);
//                            intent.putExtra("token", token);
//                            startActivity(intent);
//                        } else if (activityName.equals("FavouriteRoadActivity")){
//                            Intent intent = new Intent(this, FavouriteRoadActivity.class);
//                            intent.putExtra("favoriteRoutes", favoriteRoutes);
//                            intent.putExtra("token", token);
//                            startActivity(intent);
//                        }
//
//                    }catch (Exception e) {
//                        e.printStackTrace();
//                        Log.w(TAG, "request response:failed message=" + e.getMessage());
//                    }
//                },
//                error -> {
//                    Toast.makeText(context, getString(R.string.request_error_response_msg), Toast.LENGTH_LONG)
//                            .show();
//                    Log.w(TAG, "request response:failed time=" + error.getNetworkTimeMs());
//                    Log.w(TAG, "request response:failed msg=" + error.getMessage());
//                }
//        ) {
//            /** Passing some request headers* */
//            @Override
//            public Map getHeaders() throws AuthFailureError {
//                HashMap headers = new HashMap();
//                headers.put("authorization", "Bearer " + token);
//                return headers;
//            }
//        };
//        requestQueue.add(jsonObjectRequest);
//    }


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


