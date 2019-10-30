package com.sonpm_cloud.explorea;

import android.util.Pair;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.LinkedHashMap;
import java.util.Map;

public class Activity5_PageAdapter extends FragmentPagerAdapter {

    Map<String, Fragment> fragments;

    public Activity5_PageAdapter(FragmentManager fragmentManager, Pair<String, Fragment>... fragments) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragments = new LinkedHashMap<>();
        for (Pair<String, Fragment> fragment : fragments) {
            this.fragments.put(fragment.first, fragment.second);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.values().toArray(new Fragment[0])[position];
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.keySet().toArray(new String[0])[position];
    }
}
