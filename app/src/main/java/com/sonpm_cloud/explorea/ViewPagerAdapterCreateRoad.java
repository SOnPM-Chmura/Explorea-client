package com.sonpm_cloud.explorea;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapterCreateRoad extends FragmentPagerAdapter {

    private int COUNT = 2;

    ViewPagerAdapterCreateRoad(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new FragmentActivityMarkingPoints();
                break;
            case 1:
                fragment = new FragmentActivityPointsList();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0)
            return "Marking points";
        else
            return "Points list";
    }
}
