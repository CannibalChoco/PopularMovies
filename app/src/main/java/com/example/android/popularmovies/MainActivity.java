package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.Utils.NetworkUtils;
import com.example.android.popularmovies.Utils.PopularMoviesPreferences;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        android.support.v4.app.LoaderManager.LoaderCallbacks<List<Movie>>,
        MovieAdapter.GridItemListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_MOVIE = "movie";

    private static final int MOVIE_LOADER_ID = 1;

    private String apiKey;
    private List<Movie> movies;

    private RecyclerView recyclerView;
    private TextView emptyStateTextView;
    private ProgressBar progressBar;
    private MovieAdapter adapter;

    private SharedPreferences preferences;
    private static String prefSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setElevation(10f);
        }

        apiKey = BuildConfig.MOVIE_DB_API_KEY;

        recyclerView = findViewById(R.id.gridView);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);
        progressBar = findViewById(R.id.progressBar);

        GridLayoutManager layoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.grid_columns));
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MovieAdapter(this, new ArrayList<Movie>(), this);
        recyclerView.setAdapter(adapter);

        preferences = getSharedPreferences(PopularMoviesPreferences.PREFS_POPULAR_MOVIES, 0);
        prefSortOrder = preferences.getString(PopularMoviesPreferences.PREFS_SORT_ORDER,
                PopularMoviesPreferences.PREFS_SORT_DEFAULT);

        searchMovies();
    }

    @Override
    public void onStart() {
        super.onStart();
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new MovieLoader(this, args, apiKey);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> movieData) {
        adapter.clear();

        if (movieData != null && !movieData.isEmpty()) {
            if (movies != null) {
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
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
        adapter.clear();
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void searchMovies() {
        showLoading();
        if (isConnected()) {
            Bundle args = new Bundle();

            switch (prefSortOrder) {
                case PopularMoviesPreferences.PREFS_SORT_POPULAR:
                    args.putString(NetworkUtils.PATH_KEY, NetworkUtils.PATH_POPULAR);
                    break;
                case PopularMoviesPreferences.PREFS_SORT_RATINGS:
                    args.putString(NetworkUtils.PATH_KEY, NetworkUtils.PATH_TOP_RATED);
                    break;
            }
            LoaderManager loaderManager = getSupportLoaderManager();
            if (loaderManager != null) {
                loaderManager.restartLoader(MOVIE_LOADER_ID, args, this);
            } else {
                loaderManager.initLoader(MOVIE_LOADER_ID, args, this);
            }

            setTitleToSortOrder();

        } else {
            showEmptyState();
            emptyStateTextView.setText(R.string.empty_state_no_connection);

            setTitle(getString(R.string.app_name));
        }
    }

    private void showMovies() {
        progressBar.setVisibility(View.GONE);
        emptyStateTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyStateTextView.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyStateTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGridItemClick(int position) {
        launchDetailActivity(position);
    }

    private void launchDetailActivity(int position) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        Movie movie = movies.get(position);
        intent.putExtra(KEY_MOVIE, movie);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort_highest_rated:
                prefSortOrder = PopularMoviesPreferences.PREFS_SORT_RATINGS;
                break;
            case R.id.action_sort_most_popular:
                prefSortOrder = PopularMoviesPreferences.PREFS_SORT_POPULAR;
                break;
        }

        updatePreferences();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        searchMovies();
    }

    private void updatePreferences() {
        SharedPreferences.Editor preferenceEditor = preferences.edit();
        preferenceEditor.putString(PopularMoviesPreferences.PREFS_SORT_ORDER, prefSortOrder);
        preferenceEditor.apply();
    }

    private void setTitleToSortOrder() {
        switch (prefSortOrder) {
            case PopularMoviesPreferences.PREFS_SORT_POPULAR:
                setTitle(getString(R.string.pref_sort_label_popular));
                break;
            case PopularMoviesPreferences.PREFS_SORT_RATINGS:
                setTitle(getString(R.string.pref_sort_label_highest_rated));
        }
    }
}
