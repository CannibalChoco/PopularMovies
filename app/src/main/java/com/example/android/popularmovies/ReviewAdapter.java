package com.example.android.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private static final int MAX_LINES_REVIEW_COLLAPSED = 5;
    private static final int MAX_LINES_REVIEW_EXPANDED = Integer.MAX_VALUE;

    private final List<MovieReview> reviews;

    public ReviewAdapter (List<MovieReview> reviews){
        this.reviews = reviews;
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
        holder.expandableIndicatorTv.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvReview) TextView reviewTv;
        @BindView(R.id.tvAuthor) TextView author;
        @BindView(R.id.tvExpandableIndicator) TextView expandableIndicatorTv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        int shownLines = reviewTv.getLineCount();

                        if (shownLines > MAX_LINES_REVIEW_COLLAPSED){
                            // is expanded, set to collapsed
                            reviewTv.setMaxLines(MAX_LINES_REVIEW_COLLAPSED);
                            reviewTv.setEllipsize(TextUtils.TruncateAt.END);
                            //expandableIndicatorTv.setText(R.string.expandable_text_more);
                        } else {
                            // is collapsed, set to expanded
                            reviewTv.setMaxLines(MAX_LINES_REVIEW_EXPANDED);
                            reviewTv.setEllipsize(null);
                            //expandableIndicatorTv.setText(R.string.expandable_text_less);
                        }
                }
            });
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
