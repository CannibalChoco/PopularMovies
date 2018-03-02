package com.example.android.popularmovies;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.popularmovies.Utils.NetworkUtils;

import java.util.List;

/**
 * Created by Emils on 02.03.2018.
 */

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    private String path;
    private String apiKey;

    public MovieLoader(Context context, String path, String apiKey) {
        super(context);
        this.path = path;
        this.apiKey = apiKey;
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

