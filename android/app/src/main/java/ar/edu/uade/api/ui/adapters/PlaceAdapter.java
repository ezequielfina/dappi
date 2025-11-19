package ar.edu.uade.api.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import ar.edu.uade.api.R;
import data.dto.PlaceResponse;


import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private List<PlaceResponse> places = new ArrayList<>();
    private Context context;

    public PlaceAdapter(Context context) {
        this.context = context;
    }

    public void setPlaces(List<PlaceResponse> places) {
        this.places = places;
        notifyDataSetChanged();
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_place_card, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        PlaceResponse place = places.get(position);

        holder.tvTitle.setText(place.getName());
        holder.tvRating.setText("⭐");
        holder.tvLocation.setText("Buenos Aires");
        holder.tvStatus.setText("Abierto");

        Glide.with(context)
                .load(place.getUrl())
                .placeholder(R.drawable.ic_home)
                .into(holder.imgPlace);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPlace;
        TextView tvTitle, tvRating, tvLocation, tvStatus;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            imgPlace = itemView.findViewById(R.id.imgPlace);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
