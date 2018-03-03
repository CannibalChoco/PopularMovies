package com.example.android.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.Utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        android.support.v4.app.LoaderManager.LoaderCallbacks<List<Movie>>{

    private int MOVIE_LOADER_ID = 1;

    private String apiKey;
//    private List<Movie> movies;

    private RecyclerView recyclerView;
    private TextView emptyStateTextView;
    private ProgressBar progressBar;
    private GridLayoutManager layoutManager;
    private MovieAdapter adapter;

//    private String jsonResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiKey = getString(R.string.API_KEY);

        recyclerView = findViewById(R.id.gridview);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);
        progressBar = findViewById(R.id.progressBar);

        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MovieAdapter(this, new ArrayList<Movie>());
        recyclerView.setAdapter(adapter);

        searchMovies();
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new MovieLoader(this, NetworkUtils.PATH_TOP_RATED, apiKey);
    }


    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movieData) {
        adapter.clear();

        if (movieData != null && !movieData.isEmpty()) {
            adapter.addAll(movieData);
            showMovies();
        } else {
            showEmptyState();
            emptyStateTextView.setText(R.string.empty_state_default);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        adapter.clear();
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    private void searchMovies() {
        if (isConnected()) {
            LoaderManager loaderManager = getSupportLoaderManager();
            if (loaderManager != null) {
                loaderManager.restartLoader(MOVIE_LOADER_ID, null, this);
            } else {
                loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
            }
        } else {
            showEmptyState();
            emptyStateTextView.setText(R.string.empty_state_no_connection);
        }
    }

    private void showMovies(){
        progressBar.setVisibility(View.GONE);
        emptyStateTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading(){
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyStateTextView.setVisibility(View.GONE);
    }

    private void showEmptyState(){
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyStateTextView.setVisibility(View.VISIBLE);
    }
}
