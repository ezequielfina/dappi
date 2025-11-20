package ar.edu.uade.api.ui.adapters;

import android.content.Context;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ar.edu.uade.api.R;
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
    private Map<Long, Integer> voteStates = new HashMap<>();

    public PlaceAdapter(Context context) {
        this.context = context;
        this.sessionManager = SessionManager.getInstance(context);
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
        PlaceResponse place = placesFiltered.get(position); // USAR LA LISTA FILTRADA

        holder.tvPlaceName.setText(place.getName());
        holder.tvPlaceLocation.setText(place.getFullAddress());

        Glide.with(context).load(place.getUrl()).into(holder.ivPlaceImage);

        loadTopReviewForCard(place.getId(), holder);
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
                    holder.tvTopReviewVotes.setText("👍 " + review.getReviewVotes() + " votos");
                    holder.tvVoteCount.setText(String.valueOf(review.getReviewVotes()));

                    updateVoteButtonsUI(holder, review.getId());
                    setupVoteButtons(holder, review);

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
        holder.btnUpvote.setOnClickListener(v -> handleUpvote(holder, review));
        holder.btnDownvote.setOnClickListener(v -> handleDownvote(holder, review));
    }

    private void handleUpvote(PlaceViewHolder holder, ReviewResponse review) {
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(context, "Token no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        holder.btnUpvote.setEnabled(false);
        holder.btnDownvote.setEnabled(false);

        Call<ReviewResponse> call = RetrofitClient.getReviewApi(token).upVoteReview(review.getId());

        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                holder.btnUpvote.setEnabled(true);
                holder.btnDownvote.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ReviewResponse updatedReview = response.body();

                    holder.tvVoteCount.setText(String.valueOf(updatedReview.getReviewVotes()));
                    holder.tvTopReviewVotes.setText("👍 " + updatedReview.getReviewVotes() + " votos");

                    voteStates.put(review.getId(), 1);
                    updateVoteButtonsUI(holder, review.getId());

                    Toast.makeText(context, "¡Voto positivo registrado!", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 400) {
                    Toast.makeText(context, "Ya votaste positivamente esta review", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error al votar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                holder.btnUpvote.setEnabled(true);
                holder.btnDownvote.setEnabled(true);
                Log.e(TAG, "Error upvoting: " + t.getMessage());
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleDownvote(PlaceViewHolder holder, ReviewResponse review) {
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(context, "Token no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        holder.btnUpvote.setEnabled(false);
        holder.btnDownvote.setEnabled(false);

        Call<ReviewResponse> call = RetrofitClient.getReviewApi(token).downVoteReview(review.getId());

        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                holder.btnUpvote.setEnabled(true);
                holder.btnDownvote.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ReviewResponse updatedReview = response.body();

                    holder.tvVoteCount.setText(String.valueOf(updatedReview.getReviewVotes()));
                    holder.tvTopReviewVotes.setText("👍 " + updatedReview.getReviewVotes() + " votos");

                    voteStates.put(review.getId(), -1);
                    updateVoteButtonsUI(holder, review.getId());

                    Toast.makeText(context, "Voto negativo registrado", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 400) {
                    Toast.makeText(context, "Ya votaste negativamente esta review", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error al votar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                holder.btnUpvote.setEnabled(true);
                holder.btnDownvote.setEnabled(true);
                Log.e(TAG, "Error downvoting: " + t.getMessage());
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateVoteButtonsUI(PlaceViewHolder holder, Long reviewId) {
        Integer voteState = voteStates.get(reviewId);
        if (voteState == null) voteState = 0;

        int blueColor = ContextCompat.getColor(context, android.R.color.holo_blue_light);
        int grayColor = ContextCompat.getColor(context, android.R.color.darker_gray);

        if (voteState == 1) {
            holder.tvUpvoteIcon.setTextColor(blueColor);
            holder.tvDownvoteIcon.setTextColor(grayColor);
        } else if (voteState == -1) {
            holder.tvUpvoteIcon.setTextColor(grayColor);
            holder.tvDownvoteIcon.setTextColor(blueColor);
        } else {
            holder.tvUpvoteIcon.setTextColor(grayColor);
            holder.tvDownvoteIcon.setTextColor(grayColor);
        }
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder {
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
    }
}