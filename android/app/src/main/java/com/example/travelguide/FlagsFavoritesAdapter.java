package com.example.travelguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FlagsFavoritesAdapter extends RecyclerView.Adapter<FlagsFavoritesAdapter.FlagViewHolder> {

    private List<Integer> flagImageResIds; // List of drawable resource IDs for flags
    private Context context;

    public FlagsFavoritesAdapter(Context context, List<Integer> flagImageResIds) {
        this.context = context;
        this.flagImageResIds = flagImageResIds;
    }

    @NonNull
    @Override
    public FlagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_place_thumb, parent, false);
        return new FlagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlagViewHolder holder, int position) {
        Integer imageResId = flagImageResIds.get(position);
        holder.imgPlace.setImageResource(imageResId); // Assuming imgPlace is still the ID in item_place_thumb
    }

    @Override
    public int getItemCount() {
        return flagImageResIds != null ? flagImageResIds.size() : 0;
    }

    static class FlagViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPlace; // Assuming imgPlace is the ID of the ImageView in your item layout

        public FlagViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPlace = itemView.findViewById(R.id.imgPlace);
        }
    }

    // Optional: A method to update the data in the adapter
    public void updateData(List<Integer> newImageResIds) {
        this.flagImageResIds = newImageResIds;
        notifyDataSetChanged(); // Notifies the RecyclerView to refresh
    }
}
