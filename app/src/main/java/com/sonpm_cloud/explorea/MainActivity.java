package com.sonpm_cloud.explorea;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();
    private String url = "https://explorea-server.azurewebsites.net/greeting?name=";
    public RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity3_main_roadlist);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null
        LoginActivity.account = GoogleSignIn.getLastSignedInAccount(this);
        if (LoginActivity.account == null) {
            launchLoginActivity();
        }

//        findViewById(R.id.send_request_button).setOnClickListener(v -> sendRequest());
//        requestQueue = Volley.newRequestQueue(this);

        findViewById(R.id.buttonRoad1).setOnClickListener(v -> startActivity(new Intent(v.getContext(), RoadActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean result = false;

        //noinspection SimplifiableIfStatement
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

    private void sendRequest() {
        Context context = this;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url + LoginActivity.account.getEmail(),
                null,
                response -> {
                    try {
                        Toast.makeText(context, response.getString("massage"), Toast.LENGTH_LONG)
                                .show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.w(TAG, "request response:failed message=" + e.getMessage());
                    }
                }, error -> {
            Toast.makeText(context, getString(R.string.request_error_response_msg), Toast.LENGTH_LONG)
                    .show();
            Log.w(TAG, "request response:failed message=" + error.getNetworkTimeMs());
        }
        );

        requestQueue.add(jsonObjectRequest);
    }
}
