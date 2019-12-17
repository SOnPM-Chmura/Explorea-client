package com.sonpm_cloud.explorea.A5_CreateRoad;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.sonpm_cloud.explorea.R;

public class CreateRoadActivity extends AppCompatActivity {

    Fragment fragment1;
    Fragment fragment2;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapterCreateRoad adapter;
    FragmentManager fragmentManager;
    private boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity5_createroad);

        connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        connected = connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable()
                && connectivityManager.getActiveNetworkInfo().isConnected();
        if (connected) {
            tabLayout = findViewById(R.id.tab_layout);
            viewPager = findViewById(R.id.viewPager);
            fragmentManager = getSupportFragmentManager();
        }
        else {
            Toast.makeText(this, getString(R.string.no_network_connection), Toast.LENGTH_LONG)
                    .show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (tabLayout != null && connected) {
            fragment1 = FragmentActivityMarkingPoints.newInstance();
            fragment2 = FragmentActivityPointsList.newInstance();
            adapter = new ViewPagerAdapterCreateRoad(fragmentManager,
                    Pair.create(getString(R.string.select_points), fragment1),
                    Pair.create(getString(R.string.order_points), fragment2));
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setOffscreenPageLimit(adapter.fragments.size());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}