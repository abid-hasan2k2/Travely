package com.example.travely;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private Context context;
    private List<BannerItem> bannerItems;

    public BannerAdapter(Context context, List<BannerItem> bannerItems) {
        this.context = context;
        this.bannerItems = bannerItems;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        BannerItem bannerItem = bannerItems.get(position);
        holder.bind(bannerItem);
    }

    @Override
    public int getItemCount() {
        return bannerItems.size();
    }

    public class BannerViewHolder extends RecyclerView.ViewHolder {

        ImageView bannerImageView;
        TextView eventStartTextView;
        TextView eventNameTextView;
        TextView eventLocationTextView;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImageView = itemView.findViewById(R.id.bannerImageView);
            eventStartTextView = itemView.findViewById(R.id.event_start);
            eventNameTextView = itemView.findViewById(R.id.event_name);
            eventLocationTextView = itemView.findViewById(R.id.event_location);
        }

        public void bind(BannerItem bannerItem) {
            bannerImageView.setImageResource(bannerItem.getImageResource());
            eventStartTextView.setText(bannerItem.getEventStart());
            eventNameTextView.setText(bannerItem.getEventName());
            eventLocationTextView.setText(bannerItem.getEventLocation());

            // Handle click on banner image
            itemView.setOnClickListener(v -> {
                // Pass the URL to EventFormActivity
                ((MainActivity) context).openEventFormActivity(bannerItem.getUrl());
            });
        }
    }
}
