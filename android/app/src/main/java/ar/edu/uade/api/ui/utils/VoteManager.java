package ar.edu.uade.api.ui.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ar.edu.uade.api.R;
import data.api.RetrofitClient;
import data.dto.ReviewResponse;
import data.session.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoteManager {
    private static VoteManager instance;
    private final Context context;
    private final SessionManager sessionManager;
    private final Map<Long, Integer> voteStates = new HashMap<>();
    private final Map<Long, Integer> voteCounts = new HashMap<>();

    private VoteManager(Context context) {
        this.context = context.getApplicationContext();
        this.sessionManager = SessionManager.getInstance(context);
    }

    public static synchronized VoteManager getInstance(Context context) {
        if (instance == null) {
            instance = new VoteManager(context);
        }
        return instance;
    }

    public void handleVote(long reviewId, int type, VoteCallback callback) {
        int current = voteStates.getOrDefault(reviewId, 0);
        int newState;

        if (current == type) {
            newState = 0;
        } else {
            newState = type;
        }

        sendVoteRequest(reviewId, newState, callback);
    }

    private void sendVoteRequest(long reviewId, int newState, VoteCallback callback) {
        String token = sessionManager.getToken();
        Call<ReviewResponse> call;

        if (newState == 1) {
            call = RetrofitClient.getReviewApi(token).upVoteReview(reviewId);
        } else if (newState == -1) {
            call = RetrofitClient.getReviewApi(token).downVoteReview(reviewId);
        } else {
            int previous = voteStates.getOrDefault(reviewId, 0);
            if (previous == 1) {
                call = RetrofitClient.getReviewApi(token).downVoteReview(reviewId);
            } else {
                call = RetrofitClient.getReviewApi(token).upVoteReview(reviewId);
            }
        }

        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(context, "Error al votar", Toast.LENGTH_SHORT).show();
                    return;
                }

                ReviewResponse updated = response.body();
                voteStates.put(reviewId, newState);
                voteCounts.put(reviewId, updated.getReviewVotes());

                callback.onVoteSuccess(updated, newState);
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                Log.e("VoteManager", "Vote failed", t);
                Toast.makeText(context, "No hay conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void resetStates(List<ReviewResponse> reviews) {
        voteStates.clear();
        for (ReviewResponse r : reviews) {
            voteStates.put(r.getId(), 0);
        }
    }

    public void updateVoteButtonsUI(VoteUIHolder holder, long reviewId) {
        int state = voteStates.getOrDefault(reviewId, 0);

        int neutralColor = context.getColor(R.color.black);
        int selectedColor = context.getColor(R.color.accent_blue);

        holder.getTvUpvoteIcon().setTextColor(state == 1 ? selectedColor : neutralColor);
        holder.getTvDownvoteIcon().setTextColor(state == -1 ? selectedColor : neutralColor);

        if (voteCounts.containsKey(reviewId)) {
            holder.getTvVoteCount().setText(String.valueOf(voteCounts.get(reviewId)));
        }
    }

    public boolean hasVoteState(long reviewId) {
        return voteStates.containsKey(reviewId);
    }

    public void setVoteState(long reviewId, int state) {
        voteStates.put(reviewId, state);
    }

    public interface VoteCallback {
        void onVoteSuccess(ReviewResponse updatedReview, int newState);
    }

    public interface VoteUIHolder {
        android.widget.TextView getTvUpvoteIcon();
        android.widget.TextView getTvDownvoteIcon();
        android.widget.TextView getTvVoteCount();
    }
}
