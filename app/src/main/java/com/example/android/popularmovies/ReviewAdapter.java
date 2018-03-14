package com.example.android.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final List<MovieReview> reviews;

    public ReviewAdapter (List<MovieReview> reviews){
        this.reviews = reviews;
        Log.d("REVIEWS", reviews.toString());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_review, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MovieReview review = reviews.get(position);

        String reviewText = review.getReview();
        String reviewersName = review.getAuthor();

        holder.reviewTv.setText(reviewText);
        holder.author.setText(reviewersName);
    }

    @Override
    public int getItemCount() {
        return reviews != null ? reviews.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvReview) TextView reviewTv;
        @BindView(R.id.tvAuthor) TextView author;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * Clear the adapter
     */
    public void clear() {
        int size = getItemCount();
        this.reviews.clear();
        notifyItemRangeRemoved(0, size);
    }

    /**
     * adds all reviews to the adapter
     */
    public void addAll(List<MovieReview> reviews) {
        this.reviews.addAll(reviews);
        notifyDataSetChanged();
    }
}
