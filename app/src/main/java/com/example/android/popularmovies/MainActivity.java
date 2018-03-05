package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.android.popularmovies.Utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        android.support.v4.app.LoaderManager.LoaderCallbacks<List<Movie>>,
        MovieAdapter.GridItemListener{

    private int MOVIE_LOADER_ID = 1;

    private String apiKey;
    private List<Movie> movies;

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

        setTitle("Discover");

        apiKey = getString(R.string.API_KEY);
        movies = new ArrayList<>();

        recyclerView = findViewById(R.id.gridview);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);
        progressBar = findViewById(R.id.progressBar);

        layoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.grid_columns));
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MovieAdapter(this, new ArrayList<Movie>(), this);
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
            if (movies != null){
                movies.clear();
                movies.addAll(movieData);
            } else {
                movies = movieData;
            }

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
        showLoading();
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

    @Override
    public void onGridItemClick(int position) {
        Toast.makeText(this, String.valueOf(position), Toast.LENGTH_SHORT).show();
        launchDetailActivity(position);
    }

    private void launchDetailActivity(int position) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        Movie movie = movies.get(position);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }
}
