package com.example.android.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.popularmovies.Utils.NetworkUtils;

import java.util.List;


class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    private final String path;
    private final int id;

    public MovieLoader(Context context, Bundle args) {
        super(context);

        if (args != null && args.containsKey(NetworkUtils.PATH_KEY)) {
            path = args.getString(NetworkUtils.PATH_KEY);
        } else {
            path = NetworkUtils.PATH_POPULAR;
        }

        if (args != null && args.containsKey(NetworkUtils.ID_KEY)) {
            id = args.getInt(NetworkUtils.ID_KEY);
        } else {
            id = NetworkUtils.NO_MOVIE_ID;
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        if (id != NetworkUtils.NO_MOVIE_ID){
            return NetworkUtils.fetchReviews(id);
        } else {
            return NetworkUtils.fetchMovieData(path);
        }

    }
}

