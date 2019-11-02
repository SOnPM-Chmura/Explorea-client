package com.sonpm_cloud.explorea;

import android.os.Bundle;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class Activity5 extends AppCompatActivity {

    Fragment fragment1;
    Fragment fragment2;
    TabLayout tabLayout;
    ViewPager viewPager;
    Activity5_PageAdapter adapter;
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
            fragment1 = Activity5_Fragment1.newInstance();
            fragment2 = Activity5_Fragment2.newInstance();
            adapter = new Activity5_PageAdapter(fragmentManager,
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
