package com.example.android.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.popularmovies.Utils.NetworkUtils;

import java.util.List;


class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    private final String path;
    private final String apiKey;

    public MovieLoader(Context context, Bundle args, String apiKey) {
        super(context);
        this.apiKey = apiKey;

        if (args != null && args.containsKey(NetworkUtils.PATH_KEY)) {
            path = args.getString(NetworkUtils.PATH_KEY);
        } else {
            path = NetworkUtils.PATH_POPULAR;
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        return NetworkUtils.fetchMovieData(path, apiKey);
    }
}

