package ar.edu.uade.api.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import ar.edu.uade.api.R;
import ar.edu.uade.api.ui.places.Place;
import ar.edu.uade.api.ui.utils.VoteManager;
import data.api.RetrofitClient;
import data.dto.PlaceResponse;
import data.dto.ReviewResponse;
import data.session.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private static final String TAG = "PlaceAdapter";
    private Context context;
    private List<PlaceResponse> places = new ArrayList<>();
    private List<PlaceResponse> placesFiltered = new ArrayList<>(); // NUEVA LISTA FILTRADA
    private SessionManager sessionManager;
    private VoteManager voteManager;  // Agregado: instancia de VoteManager

    public PlaceAdapter(Context context) {
        this.context = context;
        this.sessionManager = SessionManager.getInstance(context);
        this.voteManager = VoteManager.getInstance(context);  // Inicializar VoteManager
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_place_card, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        PlaceResponse place = placesFiltered.get(position);

        holder.tvPlaceName.setText(place.getName());
        holder.tvPlaceLocation.setText(place.getFullAddress());

        Glide.with(context).load(place.getUrl()).into(holder.ivPlaceImage);

        loadTopReviewForCard(place.getId(), holder);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Place.class);

            // Pasar todos los datos del lugar
            intent.putExtra("place_id", place.getId());
            intent.putExtra("place_name", place.getName());
            intent.putExtra("place_description", place.getDescription());
            intent.putExtra("place_url", place.getUrl());
            intent.putExtra("place_latitude", place.getLatitude());
            intent.putExtra("place_longitude", place.getLongitude());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return placesFiltered.size(); // USAR LA LISTA FILTRADA
    }

    public void setPlaces(List<PlaceResponse> places) {
        this.places = places;
        this.placesFiltered = new ArrayList<>(places); // INICIALIZAR LISTA FILTRADA
        notifyDataSetChanged();
    }

    // NUEVO MÉTODO PARA FILTRAR
    public void filter(String query) {
        placesFiltered.clear();

        if (query == null || query.isEmpty()) {
            // Si no hay búsqueda, mostrar todos
            placesFiltered.addAll(places);
        } else {
            String queryLower = query.toLowerCase();

            for (PlaceResponse place : places) {
                // Buscar en nombre, dirección o categoría
                boolean matchName = place.getName() != null &&
                        place.getName().toLowerCase().contains(queryLower);
                boolean matchAddress = place.getFullAddress() != null &&
                        place.getFullAddress().toLowerCase().contains(queryLower);
                boolean matchCategory = place.getPlaceCategory() != null &&
                        place.getPlaceCategory().toLowerCase().contains(queryLower);

                if (matchName || matchAddress || matchCategory) {
                    placesFiltered.add(place);
                }
            }
        }

        notifyDataSetChanged();
        Log.d(TAG, "Filtered places: " + placesFiltered.size() + " of " + places.size());
    }

    // Resto de tus métodos existentes...
    private void loadTopReviewForCard(Long placeId, PlaceViewHolder holder) {
        holder.progressTopReview.setVisibility(View.VISIBLE);
        holder.layoutTopReview.setVisibility(View.GONE);
        holder.tvNoReviews.setVisibility(View.GONE);
        holder.dividerTopReview.setVisibility(View.GONE);

        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            holder.progressTopReview.setVisibility(View.GONE);
            Log.e(TAG, "No auth token found");
            return;
        }

        Call<ReviewResponse> call = RetrofitClient.getReviewApi(token).getTopReviewByPlace(placeId);

        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                holder.progressTopReview.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    ReviewResponse review = response.body();

                    holder.dividerTopReview.setVisibility(View.VISIBLE);
                    holder.layoutTopReview.setVisibility(View.VISIBLE);

                    holder.tvTopReviewDescription.setText(review.getDescription());
                    holder.tvTopReviewRating.setText("⭐ " + review.getRateToPlace() + "/5");
                    holder.tvVoteCount.setText(String.valueOf(review.getReviewVotes()));

                    setupVoteButtons(holder, review);
                    voteManager.updateVoteButtonsUI(holder, review.getId());  // Actualizar UI inicial con VoteManager

                    Log.d(TAG, "Top review loaded for place " + placeId);
                } else if (response.code() == 204) {
                    holder.dividerTopReview.setVisibility(View.VISIBLE);
                    holder.tvNoReviews.setVisibility(View.VISIBLE);
                    Log.d(TAG, "No reviews found for place " + placeId);
                } else {
                    Log.e(TAG, "Error loading top review: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                holder.progressTopReview.setVisibility(View.GONE);
                Log.e(TAG, "Failed to load top review: " + t.getMessage());
            }
        });
    }

    private void setupVoteButtons(PlaceViewHolder holder, ReviewResponse review) {
        holder.btnUpvote.setOnClickListener(v -> voteManager.handleVote(review.getId(), 1, new VoteManager.VoteCallback() {
            @Override
            public void onVoteSuccess(ReviewResponse updated, int newState) {
                holder.tvVoteCount.setText(String.valueOf(updated.getReviewVotes()));

                voteManager.updateVoteButtonsUI(holder, review.getId());
                Toast.makeText(context, "¡Voto positivo registrado!", Toast.LENGTH_SHORT).show();
            }
        }));
        holder.btnDownvote.setOnClickListener(v -> voteManager.handleVote(review.getId(), -1, new VoteManager.VoteCallback() {
            @Override
            public void onVoteSuccess(ReviewResponse updated, int newState) {
                holder.tvVoteCount.setText(String.valueOf(updated.getReviewVotes()));

                voteManager.updateVoteButtonsUI(holder, review.getId());
                Toast.makeText(context, "Voto negativo registrado", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    // QUITADO: handleUpvote, handleDownvote, updateVoteButtonsUI (ahora en VoteManager)

    static class PlaceViewHolder extends RecyclerView.ViewHolder implements VoteManager.VoteUIHolder {
        ImageView ivPlaceImage;
        TextView tvPlaceName;
        TextView tvPlaceLocation;
        LinearLayout layoutTopReview;
        TextView tvTopReviewDescription;
        TextView tvTopReviewRating;
        TextView tvTopReviewVotes;
        ProgressBar progressTopReview;
        TextView tvNoReviews;
        View dividerTopReview;
        LinearLayout btnUpvote;
        LinearLayout btnDownvote;
        TextView tvUpvoteIcon;
        TextView tvDownvoteIcon;
        TextView tvVoteCount;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlaceImage = itemView.findViewById(R.id.imgPlace);
            tvPlaceName = itemView.findViewById(R.id.tvTitle);
            tvPlaceLocation = itemView.findViewById(R.id.tvLocation);
            layoutTopReview = itemView.findViewById(R.id.layoutTopReview);
            tvTopReviewDescription = itemView.findViewById(R.id.tvTopReviewDescription);
            tvTopReviewRating = itemView.findViewById(R.id.tvTopReviewRating);
            tvTopReviewVotes = itemView.findViewById(R.id.tvTopReviewVotes);
            progressTopReview = itemView.findViewById(R.id.progressTopReview);
            tvNoReviews = itemView.findViewById(R.id.tvNoReviews);
            dividerTopReview = itemView.findViewById(R.id.dividerTopReview);
            btnUpvote = itemView.findViewById(R.id.btnUpvote);
            btnDownvote = itemView.findViewById(R.id.btnDownvote);
            tvUpvoteIcon = itemView.findViewById(R.id.tvUpvoteIcon);
            tvDownvoteIcon = itemView.findViewById(R.id.tvDownvoteIcon);
            tvVoteCount = itemView.findViewById(R.id.tvVoteCount);
        }

        @Override
        public TextView getTvUpvoteIcon() { return tvUpvoteIcon; }

        @Override
        public TextView getTvDownvoteIcon() { return tvDownvoteIcon; }

        @Override
        public TextView getTvVoteCount() { return tvVoteCount; }
    }
}
