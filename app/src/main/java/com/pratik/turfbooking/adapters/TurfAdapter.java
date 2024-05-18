package com.pratik.turfbooking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pratik.turfbooking.R;
import com.pratik.turfbooking.models.Turf;

import java.util.List;

// TurfAdapter.java
public class TurfAdapter extends ArrayAdapter<Turf> {
    public TurfAdapter(Context context, List<Turf> turfs) {
        super(context, 0, turfs);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Turf turf = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_turf, parent, false);
        }

        ImageView turfImage = convertView.findViewById(R.id.turfImage);
        TextView turfName = convertView.findViewById(R.id.turfName);

        if (turf != null) {
            turfImage.setImageResource(Integer.parseInt(turf.getImageUrl()));
            turfName.setText(turf.getName());
        }

        return convertView;
    }
}
