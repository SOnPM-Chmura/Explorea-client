package com.sonpm_cloud.explorea.A4_SearchRoad;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.sonpm_cloud.explorea.A4_2_RoadActivity.RoadActivity;
import com.sonpm_cloud.explorea.R;
import com.sonpm_cloud.explorea.data_classes.Route;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchRoadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "TAG";
    private String url = "https://explorea-server.azurewebsites.net";
    private RequestQueue requestQueue;
    private boolean connected;

    private String[] transports = {"foot", "bike"};
    private String[] times = {"15 min", "30 min", "60 min", "90 min"};
    private String chosenCity;
    private String chosenTransport;
    private int chosenTime;

    private int idRoute;
    private String codedRoute;
    private double avgRating;
    private int lengthByFoot;
    private int lengthByBike;
    private int timeByFoot;
    private int timeByBike;
    private String city;

    private EditText cityText;
    private LinearLayout linearLayoutForRoads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity4_searchroad);
        requestQueue =  Volley.newRequestQueue(this);

        connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        connected = connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable()
                && connectivityManager.getActiveNetworkInfo().isConnected();

        if (connected){

            cityText = findViewById(R.id.citySelected);

            linearLayoutForRoads = findViewById(R.id.RoadButtonList);

            Spinner transportSpinner = findViewById(R.id.transportSpinner1);
            Spinner timeSpinner = findViewById(R.id.timeSpinner1);

            ArrayAdapter<String> arrayTransport = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, transports);
            arrayTransport.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            transportSpinner.setAdapter(arrayTransport);

            ArrayAdapter<String> arrayTime = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, times);
            arrayTime.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            timeSpinner.setAdapter(arrayTime);

            transportSpinner.setOnItemSelectedListener(this);
            timeSpinner.setOnItemSelectedListener(this);

            System.out.println(chosenTransport);
            System.out.println(chosenTime);

            Button buttonSearch = findViewById(R.id.buttonSearch);
            buttonSearch.setOnClickListener(v -> sendGetFilteredRoutes());
        }
        else {
            Toast.makeText(this, getString(R.string.no_network_connection), Toast.LENGTH_LONG)
                    .show();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(String.valueOf(parent).contains("transportSpinner1")) {
            chosenTransport = transports[position];
            System.out.println(chosenTransport);
        }
        else if (String.valueOf(parent).contains("timeSpinner1")) {
            chosenTime = Integer.valueOf(times[position].substring(0 , times[position].lastIndexOf(" ")));
            System.out.println(chosenTime);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if(String.valueOf(parent).contains("transportSpinner1")) {
            chosenTransport = "foot";
        }
        else if(String.valueOf(parent).contains("timeSpinner1")) {
            chosenTime = 15;
        }
    }

    private void sendGetFilteredRoutes() {
        chosenCity = String.valueOf(cityText.getText());

        if (chosenCity.equals("")) {
            Toast.makeText(this, getString(R.string.add_city_name), Toast.LENGTH_LONG).show();
        } else {
            Log.d("LOG", chosenCity + " " + chosenTime + " " + chosenTransport);

            linearLayoutForRoads.removeAllViews();
            Context context = this;
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    url + "/routes?city=" + chosenCity + "&time=" + chosenTime + "&transport=" + chosenTransport,
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

                                    Route route = new Route(idRoute,codedRoute, (float)avgRating,lengthByFoot,lengthByBike,timeByFoot,timeByBike,city);
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
                        if (!connected){
                            Toast.makeText(context, getString(R.string.no_network_connection), Toast.LENGTH_LONG)
                                    .show();
                        }else
                            Toast.makeText(context, getString(R.string.request_error_response_msg), Toast.LENGTH_LONG)
                                    .show();
                        Log.w(TAG, "request response:failed time=" + error.getNetworkTimeMs());
                        Log.w(TAG, "request response:failed msg=" + error.getMessage());
                    }
            );
            requestQueue.add(jsonArrayRequest);
        }
    }
}
