package com.pratik.turfbooking.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pratik.turfbooking.R;
import com.pratik.turfbooking.models.TurfCard;

import java.util.ArrayList;
import java.util.List;

public class TurfCardAdapter extends RecyclerView.Adapter<TurfCardAdapter.TurfCardViewHolder> {

    private List<TurfCard> turfCards;
    private List<TurfCard> filteredTurfCards;
    private OnTurfCardClickListener clickListener;

    public TurfCardAdapter(List<TurfCard> turfCards, OnTurfCardClickListener clickListener) {
        this.turfCards = turfCards;
        this.filteredTurfCards = new ArrayList<>(turfCards);
        this.clickListener = clickListener;
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterPattern = constraint.toString().toLowerCase().trim();

                List<TurfCard> filteredList = new ArrayList<>();
                for (TurfCard turfCard : turfCards) {
                    if (turfCard.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(turfCard);
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredTurfCards.clear();
                filteredTurfCards.addAll((List<TurfCard>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public TurfCard getTurfAtPosition(int position) {
        return filteredTurfCards.get(position);
    }

    public interface OnTurfCardClickListener {
        void onTurfCardClick(int position);
    }

    @NonNull
    @Override
    public TurfCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_turf_card, parent, false);
        return new TurfCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TurfCardViewHolder holder, int position) {
        TurfCard turfCard = filteredTurfCards.get(position);
        holder.bind(turfCard);
    }

    @Override
    public int getItemCount() {
        return filteredTurfCards.size();
    }

    public class TurfCardViewHolder extends RecyclerView.ViewHolder {
        TextView turfNameTextView;
        TextView turfLocationTextView;
        ImageView turfImageView;
        private ImageView imageView;
        private TextView titleTextView;
        private TextView locationTextView;

        public TurfCardViewHolder(@NonNull View itemView) {
            super(itemView);
           imageView = itemView.findViewById(R.id.imageViewTurf);
            titleTextView = itemView.findViewById(R.id.textViewTurfName);
            locationTextView = itemView.findViewById(R.id.textViewTurfLocation);
           /* turfNameTextView = itemView.findViewById(R.id.textViewTurfName);
            turfLocationTextView = itemView.findViewById(R.id.textViewTurfLocation);
            turfImageView = itemView.findViewById(R.id.imageViewTurf); */

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onTurfCardClick(position);
                    }
                }
            });
        }

        public void bind(TurfCard turfCard) {
            titleTextView.setText(turfCard.getTitle());
            locationTextView.setText(turfCard.getLocation());

            // Use Glide or your preferred library to load the image
            Glide.with(itemView)
                    .load(turfCard.getImageResource())
                    .placeholder(R.drawable.placeholder_image)
                    .into(imageView);
        }

    }
}
