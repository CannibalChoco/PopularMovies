package com.example.android.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.popularmovies.Utils.NetworkUtils;

import java.util.List;


class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    public static final int MOVIE_LOADER_ID = 1;
    public static final int REVIEW_LOADER_ID = 2;
    public static final int TRAILER_LOADER_ID = 3;

    private final String path;
    private final int movieId;

    public MovieLoader(Context context, Bundle args) {
        super(context);

        if (args != null && args.containsKey(NetworkUtils.PATH_KEY)) {
            path = args.getString(NetworkUtils.PATH_KEY);
        } else {
            path = NetworkUtils.PATH_POPULAR;
        }

        if (args != null && args.containsKey(NetworkUtils.ID_KEY)) {
            movieId = args.getInt(NetworkUtils.ID_KEY);
        } else {
            movieId = NetworkUtils.NO_MOVIE_ID;
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        if (movieId != NetworkUtils.NO_MOVIE_ID){
            return NetworkUtils.fetchAdditionalData(movieId, path);
        } else {
            return NetworkUtils.fetchMovieData(path);
        }

    }
}

