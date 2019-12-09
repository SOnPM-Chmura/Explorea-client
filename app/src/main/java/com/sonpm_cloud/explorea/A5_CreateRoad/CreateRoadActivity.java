package com.sonpm_cloud.explorea.A5_CreateRoad;

import android.os.Bundle;
import android.util.Pair;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity5_createroad);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);
        fragmentManager = getSupportFragmentManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tabLayout != null) {
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
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}