package com.pratik.turfbooking.adapters;
import com.pratik.turfbooking.BookingHistoryFragment;
import com.pratik.turfbooking.BookedTurfsFragment;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class BookingsAdapter extends FragmentPagerAdapter {

    public BookingsAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // Return the corresponding fragment for each tab
        switch (position) {
            case 0:
                return new BookedTurfsFragment();
            case 1:
                return new BookingHistoryFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // Number of tabs
        return 2;
    }
}
