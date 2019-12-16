package com.sonpm_cloud.explorea;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sonpm_cloud.explorea.data_classes.APIDirectionsDAO;
import com.sonpm_cloud.explorea.data_classes.Route;

public class Activity4_test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity4_test);

        Intent intent = getIntent();
        Route route = (Route) intent.getSerializableExtra("ROUTE");

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Activity4_MapFragment fragment = new Activity4_MapFragment(route);
        fragmentTransaction.replace(R.id.frameL, fragment).commit();
        findViewById(R.id.button2).setOnClickListener(v ->
                fragment.launchMap(APIDirectionsDAO.By.Bike));
    }
}
