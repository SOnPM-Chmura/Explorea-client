package com.sonpm_cloud.explorea.A2_Login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.sonpm_cloud.explorea.MainActivity;
import com.sonpm_cloud.explorea.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getCanonicalName();
    private static final int RC_SIGN_IN = 9001;
    @SuppressLint("StaticFieldLeak")
    public static GoogleSignInClient mGoogleSignInClient;

    public static GoogleSignInAccount account = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String web_id = getWeb_id(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestIdToken(web_id).build();
        // Build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.sign_in_button).setOnClickListener(v -> signIn());
    }

    public static String getWeb_id(Context context) {
        try {
            ApplicationInfo app = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                                                                              PackageManager.GET_META_DATA);
            Bundle bundle = app.metaData;
            return (String) bundle.get("com.sonpm_cloud.explorea.api.WEB_ID");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void silentSignIn(Context context,
                                    Runnable apiCallOnSuccess,
                                    String activityName) {
        GoogleSignInClient client = LoginActivity.mGoogleSignInClient == null ?
                GoogleSignIn.getClient(context, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail().requestIdToken(LoginActivity.getWeb_id(context)).build()) :
                LoginActivity.mGoogleSignInClient;
        client.silentSignIn().addOnSuccessListener(account -> {
            Log.i(activityName.substring(0, Math.min(activityName.length(), 23)), "SilentSignInSuccess ✔");
            LoginActivity.account = account;
            apiCallOnSuccess.run();
        }).addOnFailureListener(e -> Log.e(activityName.substring(0, Math.min(activityName.length(), 23)), "SilentSignInFailure ❌: " + e.getMessage()));
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {

            LoginActivity.account = completedTask.getResult(ApiException.class);
            // Signed in successfully, return to main activity
            Intent loginIntent = new Intent(this, MainActivity.class);
            startActivity(loginIntent);
        } catch (ApiException e) {

            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            LoginActivity.account = null;
        }
    }
}