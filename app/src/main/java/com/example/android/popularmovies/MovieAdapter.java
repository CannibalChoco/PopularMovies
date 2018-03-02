package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Emils on 01.03.2018.
 */

public class MovieAdapter extends ArrayAdapter<Movie> {

    Context context;
    List<Movie> movies;

    public MovieAdapter (Context context, List<Movie> movies){
        super(context, 0, movies);

        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        }

        Movie movie = getItem(position);
        String posterPath = movie.getPosterPath();

        ImageView poster = convertView.findViewById(R.id.poster_item_view);

        if (posterPath != null){
            String posterUrl = NetworkUtils.buildUrlForMoviePoster(posterPath);
            Picasso.with(context).load(posterUrl).into(poster);
        }

        return convertView;
    }


}
