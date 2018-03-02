package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.popularmovies.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.security.PublicKey;
import java.util.List;

/**
 * Created by Emils on 01.03.2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{

    Context context;
    List<Movie> movies;

        public MovieAdapter (Context context, List<Movie> movies){
        this.context = context;
        this.movies = movies;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements
                        View.OnClickListener{
        ImageView posterImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.poster_item_view);
        }

        @Override
        public void onClick(View v) {
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        String posterPath = movie.getPosterPath();

        if (posterPath != null){
            String posterUrl = NetworkUtils.buildUrlForMoviePoster(posterPath);
            Picasso.with(context).load(posterUrl).into(holder.posterImageView);
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
}
