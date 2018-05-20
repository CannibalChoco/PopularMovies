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

    private boolean[] isExpandedArray;

    public ReviewAdapter (List<MovieReview> reviews,  boolean[] isExpandedArray){
        this.reviews = reviews;

        this.isExpandedArray = new boolean[100];

        if (isExpandedArray != null){
            for (int i = 0, j = reviews.size(); i < j; i++){
                this.isExpandedArray[i] = isExpandedArray[i];
            }
        } else {
            for (int i = 0, j = reviews.size(); i < j; i++){
                this.isExpandedArray[i] = false;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_review, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
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
                        } else {
                            holder.expandableIndicator.setVisibility(View.VISIBLE);
                            holder.isExpandable = true;
                            holder.reviewTv.setMaxLines(MAX_LINES_REVIEW_COLLAPSED);
                            holder.reviewTv.setEllipsize(TextUtils.TruncateAt.END);
                            holder.expandableIndicator.setText(R.string.expandable_text_more);
                            holder.expandableIndicator.setVisibility(View.VISIBLE);
                        }

                        if (isExpandedArray[holder.getAdapterPosition()] && holder.isExpandable){
                            setExpanded(holder);
                        }
                    }
                });

        holder.reviewTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleExpandableViewState(holder);
            }
        });

        holder.expandableIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleExpandableViewState(holder);
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

        ViewHolder(final View itemView) {
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

    public boolean[] getIsExpandedArray(){
        return isExpandedArray;
    }

    /**
     * Toggle views in ViewHolder between expanded and collapsed
     * @param holder ViewHolder containing the views to be toggled
     */
    private void toggleExpandableViewState(ViewHolder holder){
        int shownLines = holder.reviewTv.getLineCount();

        if(holder.isExpandable){
            // is expanded
            if (shownLines > MAX_LINES_REVIEW_COLLAPSED) {
                setCollapsed(holder);
            } else {
                setExpanded(holder);
            }
        }
    }

    /**
     * Sets all views for the collapsed Review view state
     * @param holder ViewHolder holding views to be set
     */
    private void setCollapsed (ViewHolder holder){
        holder.reviewTv.setMaxLines(MAX_LINES_REVIEW_COLLAPSED);
        holder.reviewTv.setEllipsize(TextUtils.TruncateAt.END);
        holder.expandableIndicator.setText(R.string.expandable_text_more);
        holder.expandableIndicator.setVisibility(View.VISIBLE);
        isExpandedArray[holder.getAdapterPosition()] = false;
    }

    /**
     * Sets all views for the expanded Review view state
     * @param holder ViewHolder holding views to be set
     */
    private void setExpanded (ViewHolder holder){
        holder.reviewTv.setMaxLines(MAX_LINES_REVIEW_EXPANDED);
        holder.reviewTv.setEllipsize(null);
        holder.expandableIndicator.setText(R.string.expandable_text_less);
        holder.expandableIndicator.setVisibility(View.VISIBLE);
        isExpandedArray[holder.getAdapterPosition()] = true;
    }
}
