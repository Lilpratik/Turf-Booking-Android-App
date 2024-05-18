package com.pratik.turfbooking;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import androidx.preference.EditTextPreference;

public class AdminLoginPreference extends EditTextPreference {

    public AdminLoginPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AdminLoginPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AdminLoginPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdminLoginPreference(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {
        super.onClick();
        // Handle admin login logic here
        // For example, open AdminLoginActivity for authentication
        getContext().startActivity(new Intent(getContext(), AdminLoginActivity.class));
    }
}

