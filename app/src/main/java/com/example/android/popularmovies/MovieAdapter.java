package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.example.android.popularmovies.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private final Context context;
    private final List<Movie> movies;

    private static GridItemListener onClickListener;

    public interface GridItemListener {
        void onGridItemClick(int position);
    }

    public MovieAdapter(Context context, List<Movie> movies, GridItemListener listener) {
        this.context = context;
        this.movies = movies;
        onClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        @BindView(R.id.poster_item_view) ImageView posterImageView;
        @BindView(R.id.ratingBar) RatingBar ratingBar;
        @BindView(R.id.gridItemView) ConstraintLayout gridItem;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            onClickListener.onGridItemClick(position);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Movie movie = movies.get(position);

        holder.ratingBar.setVisibility(View.GONE);

        String posterPath = movie.getPosterPath();
        if (posterPath != null && !TextUtils.isEmpty(posterPath)) {
            String posterUrl = NetworkUtils.buildUrlForMoviePoster(posterPath);
            Picasso.with(context)
                    .load(posterUrl)
                    .into(holder.posterImageView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    holder.ratingBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {

                }
            });

            ViewGroup.LayoutParams lp = new ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            holder.gridItem.setLayoutParams(lp);

            holder.ratingBar.setRating(movie.getRatingForFiveStars());
        }
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    /**
     * Clear the adapter
     */
    public void clear() {
        int size = getItemCount();
        this.movies.clear();
        notifyItemRangeRemoved(0, size);
    }

    /**
     * adds all movies to the adapter
     */
    public void addAll(List<Movie> movies) {
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    public Movie getItem(int position) {
        return movies.get(position);
    }
}
