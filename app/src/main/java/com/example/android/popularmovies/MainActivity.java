package com.example.android.popularmovies;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
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
import android.widget.Toast;

import com.example.android.popularmovies.Utils.NetworkUtils;
import com.example.android.popularmovies.Utils.PopularMoviesPreferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * To implemet ConnectivityReceiver as per reviewer suggested tutorial on
 * https://www.androidhive.info/2012/07/android-detect-internet-connection-status/
 * some code is taken from the tutorial, which is disclosed above that code.
 */
public class MainActivity extends AppCompatActivity implements
        android.support.v4.app.LoaderManager.LoaderCallbacks<List<Movie>>,
        MovieAdapter.GridItemListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        ConnectivityReceiver.ConnectivityReceiverListener{

    @BindView(R.id.gridView) RecyclerView recyclerView;
    @BindView(R.id.emptyStateTextView) TextView emptyStateTextView;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    public static final String KEY_MOVIE = "movie";

    private static final int MOVIE_LOADER_ID = 1;

    private String apiKey;
    private List<Movie> movies;

    private MovieAdapter adapter;

    private SharedPreferences preferences;
    private static String prefSortOrder;

    private ConnectivityReceiver connectivityReceiver;

    private boolean isWaitingForInternetConnection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setElevation(10f);
        }

        apiKey = BuildConfig.MOVIE_DB_API_KEY;

        GridLayoutManager layoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.grid_columns));
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MovieAdapter(this, new ArrayList<Movie>(), this);
        recyclerView.setAdapter(adapter);

        preferences = getSharedPreferences(PopularMoviesPreferences.PREFS_POPULAR_MOVIES, 0);
        prefSortOrder = preferences.getString(PopularMoviesPreferences.PREFS_SORT_ORDER,
                PopularMoviesPreferences.PREFS_SORT_DEFAULT);

        searchMoviesIfConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();

        preferences.registerOnSharedPreferenceChangeListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            connectivityReceiver = new ConnectivityReceiver();
            this.registerReceiver(connectivityReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

        /**
         * register connection status listener
         * as shown in the androidhive tutorial
         */
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (connectivityReceiver != null){
            this.unregisterReceiver(connectivityReceiver);
        }

        preferences.unregisterOnSharedPreferenceChangeListener(this);
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

    @Override
    public void onGridItemClick(int position) {
        launchDetailActivity(position);
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
        searchMoviesIfConnected();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (isConnected){
            if (isWaitingForInternetConnection){
                searchMovies();
                isWaitingForInternetConnection = false;
            }
        } else {
            if (!isWaitingForInternetConnection){
                Toast.makeText(this, getString(R.string.connectivity_lost_message), Toast.LENGTH_LONG).show();
                isWaitingForInternetConnection = true;
            }
        }
    }

    /**
     * Get users preferred sort order from SharedPreferences and put it in a Bundle
     *
     * Used in MovieLoader as argument
     *
     * @return Bundle with preferred sort order ready to be used by MovieLoader
     */
    private Bundle getSortOrderArgsBundle (){
        Bundle args = new Bundle();

        switch (prefSortOrder) {
            case PopularMoviesPreferences.PREFS_SORT_POPULAR:
                args.putString(NetworkUtils.PATH_KEY, NetworkUtils.PATH_POPULAR);
                break;
            case PopularMoviesPreferences.PREFS_SORT_RATINGS:
                args.putString(NetworkUtils.PATH_KEY, NetworkUtils.PATH_TOP_RATED);
                break;
        }

        return args;
    }

    /**
     * Preform Movie search with LoaderManager initLoader() or restartLoader()
     *
     * Used by searchMoviesIfConnected()
     */
    private void searchMovies() {
        LoaderManager loaderManager = getSupportLoaderManager();
        if (loaderManager != null) {
            loaderManager.restartLoader(MOVIE_LOADER_ID, getSortOrderArgsBundle(), this);
        } else {
            loaderManager.initLoader(MOVIE_LOADER_ID, getSortOrderArgsBundle(), this);
        }
    }

    /**
     * Preform all necessary search related actions if connected, or all emptyState actions when
     * there is no internet connection
     */
    private void searchMoviesIfConnected() {
        if (ConnectivityReceiver.isConnected()) {
            showLoading();
            searchMovies();
            setTitleToSortOrder();
        } else {
            isWaitingForInternetConnection = true;
            showEmptyState();
            emptyStateTextView.setText(R.string.empty_state_no_connection);
            setTitle(getString(R.string.app_name));
        }
    }

    /**
     * Set gridView visible and hide all other views
     */
    private void showMovies() {
        progressBar.setVisibility(View.GONE);
        emptyStateTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Set progressBar visible and hide all other views
     */
    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyStateTextView.setVisibility(View.GONE);
    }

    /**
     * Set emptyState text visible and hide all other views
     */
    private void showEmptyState() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyStateTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Updates SharedPreferences when user selects sort order from optionsMenu
     */
    private void updatePreferences() {
        SharedPreferences.Editor preferenceEditor = preferences.edit();
        preferenceEditor.putString(PopularMoviesPreferences.PREFS_SORT_ORDER, prefSortOrder);
        preferenceEditor.apply();
    }

    /**
     * Sets the ActionBar title according to sort order in which movies are displayed
     */
    private void setTitleToSortOrder() {
        switch (prefSortOrder) {
            case PopularMoviesPreferences.PREFS_SORT_POPULAR:
                setTitle(getString(R.string.pref_sort_label_popular));
                break;
            case PopularMoviesPreferences.PREFS_SORT_RATINGS:
                setTitle(getString(R.string.pref_sort_label_highest_rated));
                break;
            default:
                setTitle(getString(R.string.app_name));
        }
    }

    /**
     * Start DetailActivity when Movie poster is clicked, passing the movie data
     *
     * @param position Movie position in RecyclerView, corresponding to the
     * Movie in List<Movie>
     */
    private void launchDetailActivity(int position) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        Movie movie = movies.get(position);
        intent.putExtra(KEY_MOVIE, movie);
        startActivity(intent);
    }
}
