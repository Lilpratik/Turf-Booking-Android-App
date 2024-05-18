package com.pratik.turfbooking.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pratik.turfbooking.fragments.HomeFragment;
import com.pratik.turfbooking.fragments.BookingFragment;
import com.pratik.turfbooking.fragments.ProfileFragment;
import com.pratik.turfbooking.fragments.SettingsFragment;

public class MyPagerAdapter extends FragmentPagerAdapter {

    public MyPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // Return the appropriate Fragment based on the position
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new BookingFragment();
            case 2:
                return new ProfileFragment();
            case 3:
                return new SettingsFragment();
            default:
                // Return a default fragment or handle as needed
                return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        // Total number of pages (fragments)
        return 4; // Adjust this based on the number of fragments
    }
}
