package com.sonpm_cloud.explorea;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
        setContentView(R.layout.activity_main);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null
        LoginActivity.account = GoogleSignIn.getLastSignedInAccount(this);
        if (LoginActivity.account == null) {
            launchLoginActivity();
        }

        findViewById(R.id.sign_out_button).setOnClickListener(v -> signOut());
        findViewById(R.id.send_request_button).setOnClickListener(v -> sendRequest());
        requestQueue = Volley.newRequestQueue(this);
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
                        Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG)
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
