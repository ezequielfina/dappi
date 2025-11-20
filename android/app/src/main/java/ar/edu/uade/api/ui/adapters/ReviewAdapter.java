package ar.edu.uade.api.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ar.edu.uade.api.R;
import ar.edu.uade.api.ui.utils.VoteManager;
import data.api.RetrofitClient;
import data.dto.ReviewResponse;
import data.session.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final Context context;
    private List<ReviewResponse> reviews = new ArrayList<>();
    private final VoteManager voteManager;  // Agregado: instancia de VoteManager
    private SessionManager sessionManager;

    public ReviewAdapter(Context context) {
        this.context = context;
        this.sessionManager = SessionManager.getInstance(context);
        this.voteManager = VoteManager.getInstance(context);  // Inicializar VoteManager
    }

    public void setReviews(List<ReviewResponse> reviews) {
        this.reviews = reviews;

        // MODIFICACIÓN: No resetear voteStates, solo agregar nuevos si no existen
        for (ReviewResponse r : reviews) {
            if (!voteManager.hasVoteState(r.getId())) {  // Necesitamos un método en VoteManager para verificar
                voteManager.setVoteState(r.getId(), 0);  // O inicializar a 0 si no existe
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewResponse review = reviews.get(position);

        holder.tvDescription.setText(review.getDescription());
        holder.tvRating.setText(review.getRateToPlace() + " ⭐");
        holder.tvVoteCount.setText(String.valueOf(review.getReviewVotes()));

        setupVoteButtons(holder, review);
        voteManager.updateVoteButtonsUI(holder, review.getId());  // Actualizar UI con estados persistentes
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    private void setupVoteButtons(ReviewViewHolder holder, ReviewResponse review) {
        holder.btnUpvote.setOnClickListener(v -> voteManager.handleVote(review.getId(), 1, new VoteManager.VoteCallback() {
            @Override
            public void onVoteSuccess(ReviewResponse updated, int newState) {
                // Actualizar la lista y UI
                for (int i = 0; i < reviews.size(); i++) {
                    if (reviews.get(i).getId().equals(review.getId())) {
                        reviews.set(i, updated);
                        break;
                    }
                }
                holder.tvVoteCount.setText(String.valueOf(updated.getReviewVotes()));
                voteManager.updateVoteButtonsUI(holder, review.getId());
            }
        }));
        holder.btnDownvote.setOnClickListener(v -> voteManager.handleVote(review.getId(), -1, new VoteManager.VoteCallback() {
            @Override
            public void onVoteSuccess(ReviewResponse updated, int newState) {
                // Actualizar la lista y UI
                for (int i = 0; i < reviews.size(); i++) {
                    if (reviews.get(i).getId().equals(review.getId())) {
                        reviews.set(i, updated);
                        break;
                    }
                }
                holder.tvVoteCount.setText(String.valueOf(updated.getReviewVotes()));
                voteManager.updateVoteButtonsUI(holder, review.getId());
            }
        }));
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder implements VoteManager.VoteUIHolder {
        TextView tvDescription, tvRating, tvVoteCount;
        View btnUpvote, btnDownvote;
        TextView tvUpvoteIcon, tvDownvoteIcon;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDescription = itemView.findViewById(R.id.tvReviewDescription);
            tvRating = itemView.findViewById(R.id.tvReviewRating);

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