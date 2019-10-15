package com.sonpm_cloud.explorea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {


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

    }
}
