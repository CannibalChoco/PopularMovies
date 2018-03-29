package com.example.android.popularmovies;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        MovieReview review = reviews.get(position);

        String reviewText = review.getReview();
        String reviewersName = review.getAuthor();

        holder.reviewTv.setText(reviewText);
        holder.author.setText(reviewersName);


        /*
            Implementation for getting textviews line count inside adapter, taken from SO post reply
            https://stackoverflow.com/questions/43713121/getlinecount-on-textview-in-recyclerview-returning-zero
         */
        holder.reviewTv.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        holder.reviewTv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int lines = holder.reviewTv.getLineCount();

                        if (lines <= MAX_LINES_REVIEW_COLLAPSED){
                            holder.expandableIndicator.setVisibility(View.INVISIBLE);
                            holder.isExpandable = false;
                        } else if (lines > MAX_LINES_REVIEW_COLLAPSED){
                            holder.expandableIndicator.setVisibility(View.VISIBLE);
                            holder.isExpandable = true;
                            holder.reviewTv.setMaxLines(MAX_LINES_REVIEW_COLLAPSED);
                            holder.reviewTv.setEllipsize(TextUtils.TruncateAt.END);
                            holder.expandableIndicator.setText(R.string.expandable_text_more);
                            holder.expandableIndicator.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvReview) TextView reviewTv;
        @BindView(R.id.tvAuthor) TextView author;
        @BindView(R.id.tvExpandableIndicator) TextView expandableIndicator;
        boolean isExpandable;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        int shownLines = reviewTv.getLineCount();

                        if(isExpandable){
                            if (shownLines > MAX_LINES_REVIEW_COLLAPSED) {
                                // is expanded, set to collapsed
                                reviewTv.setMaxLines(MAX_LINES_REVIEW_COLLAPSED);
                                reviewTv.setEllipsize(TextUtils.TruncateAt.END);
                                expandableIndicator.setText(R.string.expandable_text_more);
                                expandableIndicator.setVisibility(View.VISIBLE);
                            } else {
                                // is collapsed, set to expanded
                                reviewTv.setMaxLines(MAX_LINES_REVIEW_EXPANDED);
                                reviewTv.setEllipsize(null);
                                expandableIndicator.setText(R.string.expandable_text_less);
                                expandableIndicator.setVisibility(View.VISIBLE);
                            }
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
