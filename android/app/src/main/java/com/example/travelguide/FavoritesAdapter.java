package com.example.travelguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    private List<Integer> favoritePlaceImageResIds; // List of drawable resource IDs
    private Context context;

    public FavoritesAdapter(Context context, List<Integer> favoritePlaceImageResIds) {
        this.context = context;
        this.favoritePlaceImageResIds = favoritePlaceImageResIds;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_place_thumb, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Integer imageResId = favoritePlaceImageResIds.get(position);
        holder.imgPlace.setImageResource(imageResId);
    }

    @Override
    public int getItemCount() {
        return favoritePlaceImageResIds != null ? favoritePlaceImageResIds.size() : 0;
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPlace;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPlace = itemView.findViewById(R.id.imgPlace);
        }
    }

    // Optional: A method to update the data in the adapter
    public void updateData(List<Integer> newImageResIds) {
        this.favoritePlaceImageResIds = newImageResIds;
        notifyDataSetChanged(); // Notifies the RecyclerView to refresh
    }
}
