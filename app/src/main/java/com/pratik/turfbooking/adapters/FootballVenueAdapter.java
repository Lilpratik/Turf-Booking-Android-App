package com.pratik.turfbooking.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pratik.turfbooking.R;
import com.pratik.turfbooking.TurfDetailActivity;
import com.pratik.turfbooking.models.FootballVenue;

import java.util.ArrayList;
import java.util.List;

public class FootballVenueAdapter extends RecyclerView.Adapter<FootballVenueAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<FootballVenue> footballVenues;
    private List<FootballVenue> footballVenuesFiltered;

    public FootballVenueAdapter(Context context, List<FootballVenue> footballVenues) {
        this.context = context;
        this.footballVenues = footballVenues;
        this.footballVenuesFiltered = new ArrayList<>(footballVenues);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.football_venue_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FootballVenue footballVenue = footballVenuesFiltered.get(position);
        holder.bind(footballVenue);
    }

    @Override
    public int getItemCount() {
        return footballVenuesFiltered.size();
    }

    public void filter(String query) {
        footballVenuesFiltered.clear();
        if (query.isEmpty()) {
            footballVenuesFiltered.addAll(footballVenues);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (FootballVenue venue : footballVenues) {
                if (venue.getName().toLowerCase().contains(lowerCaseQuery)) {
                    footballVenuesFiltered.add(venue);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private TextView nameTextView;
        private TextView locationTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            itemView.setOnClickListener(this);
        }

        public void bind(FootballVenue footballVenue) {
            Glide.with(context).load(footballVenue.getImageUrl()).into(imageView);
            nameTextView.setText(footballVenue.getName());
            locationTextView.setText(footballVenue.getAddress());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                FootballVenue selectedVenue = footballVenuesFiltered.get(position);
                Intent intent = new Intent(context, TurfDetailActivity.class);
                intent.putExtra("TURF_NAME", selectedVenue.getName());
                intent.putExtra("TURF_LOCATION", selectedVenue.getAddress());
                intent.putExtra("TURF_IMAGE", selectedVenue.getImageUrl());
                intent.putExtra("TURF_ID", selectedVenue.getId()); // Add this line to pass turfId
                context.startActivity(intent);
            }
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString();
                if (query.isEmpty()) {
                    footballVenuesFiltered = new ArrayList<>(footballVenues);
                } else {
                    List<FootballVenue> filteredList = new ArrayList<>();
                    for (FootballVenue venue : footballVenues) {
                        if (venue.getName().toLowerCase().contains(query.toLowerCase())) {
                            filteredList.add(venue);
                        }
                    }
                    footballVenuesFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = footballVenuesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                footballVenuesFiltered = (List<FootballVenue>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}






