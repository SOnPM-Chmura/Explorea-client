package com.sonpm_cloud.explorea;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class SearchRoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity4_searchroad);

        int screensize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        int orientation = getResources().getConfiguration().orientation;

        if((orientation == Configuration.ORIENTATION_PORTRAIT && screensize == Configuration.SCREENLAYOUT_SIZE_NORMAL)
                || (orientation == Configuration.ORIENTATION_LANDSCAPE && screensize == Configuration.SCREENLAYOUT_SIZE_NORMAL)){

            ViewPager viewPager = findViewById(R.id.viewPager);
            if (viewPager != null) {
                ViewPagerAdapterSearchRoad adapter = new ViewPagerAdapterSearchRoad(getSupportFragmentManager(), 0);
                viewPager.setAdapter(adapter);
            }

        }
        else {
            FragmentActivitySettings fragmentActivitySettings = new FragmentActivitySettings();
            FragmentActivityRoadList fragmentActivityRoadList = new FragmentActivityRoadList();
        }
    }
}
