package com.example.android.popularmovies;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.Data.MovieContract;
import com.example.android.popularmovies.Utils.DbUtils;
import com.example.android.popularmovies.Utils.NetworkUtils;
import com.example.android.popularmovies.Utils.PopularMoviesPreferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * To implement ConnectivityReceiver as per reviewer suggested tutorial on
 * https://www.androidhive.info/2012/07/android-detect-internet-connection-status/
 * some code is taken from the tutorial, which is disclosed above that code.
 */
public class MainActivity extends AppCompatActivity implements
        android.support.v4.app.LoaderManager.LoaderCallbacks<List<Movie>>,
        MovieAdapter.GridItemListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        ConnectivityReceiver.ConnectivityReceiverListener {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.gridView)
    RecyclerView recyclerView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.emptyStateTextView)
    TextView emptyStateTextView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.main_bottom_nav)
    BottomNavigationView bottomNav;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.toolBar)
    Toolbar toolbar;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.appBar) android.support.design.widget.AppBarLayout appBar;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    public static final String KEY_MOVIE = "movie";
    private static final String KEY_MOVIE_LIST = "movies";
    private static final String KEY_IS_WAITING_CONNECTION = "isWaitingConnection";

    private static final int POSTER_WIDTH = 200;
    private static final int DB_LOADER = 8;

    private List<Movie> movies;

    private MovieAdapter adapter;

    private SharedPreferences preferences;
    private static String prefSortOrder;

    private ConnectivityReceiver connectivityReceiver;

    private boolean isWaitingForInternetConnection;
    private boolean hasLoadedMovies = false;

    @SuppressWarnings("WeakerAccess")
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener;

    private final LoaderManager.LoaderCallbacks favoritesLoaderListener = new
            LoaderManager.LoaderCallbacks<Cursor>() {
                @NonNull
                @Override
                public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
                    String[] projection = new String[]{" * "};

                    return new CursorLoader(getApplicationContext(), MovieContract.MoviesEntry.CONTENT_URI,
                            projection,
                            null,
                            null,
                            null);
                }

                @Override
                public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
                    if (prefSortOrder.equals(PopularMoviesPreferences.PREFS_SORT_FAVORITES)){
                        adapter.clear();

                        if (data != null){
                            List<Movie> favorites = DbUtils.getMovieListFromCursor(data);

                            if (!favorites.isEmpty()){
                                if (movies != null){
                                    movies.clear();
                                    movies.addAll(favorites);
                                } else {
                                    movies = favorites;
                                }

                                adapter.addAll(favorites);
                                showMovies();
                            } else {
                                showEmptyState();
                                emptyStateTextView.setText("no movies added to favorites yet");
                            }


                        } else {
                            showEmptyState();
                            emptyStateTextView.setText("no movies here");
                        }
                    }
                }

                @Override
                public void onLoaderReset(@NonNull Loader<Cursor> loader) {

                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            movies = savedInstanceState.getParcelableArrayList(KEY_MOVIE_LIST);
            isWaitingForInternetConnection =
                    savedInstanceState.getBoolean(KEY_IS_WAITING_CONNECTION);
            appBar.setExpanded(false);
        } else {
            movies = new ArrayList<>();
            isWaitingForInternetConnection = false;
        }

        GridLayoutManager layoutManager = new GridLayoutManager(this,
                getGridViewSpanFromItemWidth());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MovieAdapter(this, new ArrayList<Movie>(), this);
        recyclerView.setAdapter(adapter);

        preferences = getSharedPreferences(PopularMoviesPreferences.PREFS_POPULAR_MOVIES, 0);
        prefSortOrder = preferences.getString(PopularMoviesPreferences.PREFS_SORT_ORDER,
                PopularMoviesPreferences.PREFS_SORT_DEFAULT);

        setTitleToSortOrder();

        navigationItemSelectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.action_sort_highest_rated:
                                prefSortOrder = PopularMoviesPreferences.PREFS_SORT_RATINGS;
                                break;
                            case R.id.action_sort_most_popular:
                                prefSortOrder = PopularMoviesPreferences.PREFS_SORT_POPULAR;
                                break;
                            case R.id.action_sort_favorite:
                                prefSortOrder = PopularMoviesPreferences.PREFS_SORT_FAVORITES;
                                break;
                            default:
                                prefSortOrder = PopularMoviesPreferences.PREFS_SORT_POPULAR;
                        }

                        updatePreferences();
                        return true;
                    }
                };

        bottomNav.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNav.setSelectedItemId(getSelectedBottomNavItem());

        setUpAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        preferences.registerOnSharedPreferenceChangeListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityReceiver = new ConnectivityReceiver();
            this.registerReceiver(connectivityReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

        /*
         * register connection status listener
         * as shown in the androidhive tutorial
         */
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (connectivityReceiver != null) {
            this.unregisterReceiver(connectivityReceiver);
        }

        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (movies != null) {
            outState.putParcelableArrayList(KEY_MOVIE_LIST, (ArrayList<Movie>) movies);
            outState.putBoolean(KEY_IS_WAITING_CONNECTION, isWaitingForInternetConnection);
        }

        super.onSaveInstanceState(outState);
    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new MovieLoader(this, args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> movieData) {
        if (!prefSortOrder.equals(PopularMoviesPreferences.PREFS_SORT_FAVORITES)) {
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (prefSortOrder.equals(PopularMoviesPreferences.PREFS_SORT_FAVORITES)){
            getFavoritesFromDb();
        } else {
            searchMoviesIfConnected();
            recyclerView.smoothScrollToPosition(0);
            appBar.setExpanded(true);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            if (isWaitingForInternetConnection) {
                if (!hasLoadedMovies){
                    searchMovies();
                }
                isWaitingForInternetConnection = false;
            }

        } else {
            if (!isWaitingForInternetConnection) {
                showSnackbar(getString(R.string.connectivity_lost_message));
                isWaitingForInternetConnection = true;
            }

        }
    }

    private void setUpAdapter(){
        if (movies.isEmpty()) {
            if (prefSortOrder.equals(PopularMoviesPreferences.PREFS_SORT_FAVORITES)){
                getFavoritesFromDb();
            } else {
                searchMoviesIfConnected();
            }

        } else {
            adapter.clear();
            adapter.addAll(movies);
            showMovies();
        }
    }

    /**
     * Get users preferred sort order from SharedPreferences and put it in a Bundle
     * <p>
     * Used in MovieLoader as argument
     *
     * @return Bundle with preferred sort order ready to be used by MovieLoader
     */
    private Bundle getSortOrderArgsBundle() {
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
     * <p>
     * Used by searchMoviesIfConnected() and onNetworkConnectionChanged() when connection is regained
     * when emptyStateTextView is displayed
     */
    private void searchMovies() {
        showLoading();
        LoaderManager loaderManager = getSupportLoaderManager();
        if (loaderManager != null) {
            loaderManager.restartLoader(MovieLoader.MOVIE_LOADER_ID, getSortOrderArgsBundle(), this);
        } else {
            //noinspection ConstantConditions
            loaderManager.initLoader(MovieLoader.MOVIE_LOADER_ID, getSortOrderArgsBundle(), this);
        }

    }

    private void getFavoritesFromDb(){
        showLoading();
        LoaderManager loaderManager = getSupportLoaderManager();
        if (loaderManager != null) {
            loaderManager.restartLoader(DB_LOADER, null, favoritesLoaderListener);
        } else {
            //noinspection ConstantConditions
            loaderManager.initLoader(DB_LOADER, null, favoritesLoaderListener);
        }
        setTitleToSortOrder();
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
        hasLoadedMovies = true;
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
     * Called by : 1) onLoadFinished() when movie data is empty or null
     * 2) searchMoviesIfConnected() when there is no internet connection
     */
    private void showEmptyState() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyStateTextView.setVisibility(View.VISIBLE);

        hasLoadedMovies = false;
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
                setTitle(getString(R.string.nav_sort_label_popular));
                break;
            case PopularMoviesPreferences.PREFS_SORT_RATINGS:
                setTitle(getString(R.string.nav_sort_label_highest_rated));
                break;
            case PopularMoviesPreferences.PREFS_SORT_FAVORITES:
                setTitle(getString(R.string.nav_sort_label_favorites));
                break;
            default:
                setTitle(getString(R.string.app_name));
        }
    }

    /**
     * Start DetailActivity when Movie poster is clicked, passing the movie data
     *
     * @param position Movie position in RecyclerView, corresponding to the
     *                 Movie in List<Movie>
     */
    private void launchDetailActivity(int position) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        Movie movie = movies.get(position);
        intent.putExtra(KEY_MOVIE, movie);
        startActivity(intent);
    }

    /**
     * Calculate GridView span based on preferred item width
     *
     * @return number of columns for GridView to display
     */
    private int getGridViewSpanFromItemWidth() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        float dpWidth = (displayMetrics.widthPixels / displayMetrics.densityDpi) * 160;

        return Math.round(dpWidth / POSTER_WIDTH);
    }

    private int getSelectedBottomNavItem() {
        switch (prefSortOrder){
            case PopularMoviesPreferences.PREFS_SORT_RATINGS:
                return R.id.action_sort_highest_rated;
            case PopularMoviesPreferences.PREFS_POPULAR_MOVIES:
                return R.id.action_sort_most_popular;
            case  PopularMoviesPreferences.PREFS_SORT_FAVORITES:
                return R.id.action_sort_favorite;
        }

        return R.id.action_sort_most_popular;
    }

    private void showSnackbar(String message){
        Snackbar snack = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                snack.getView().getLayoutParams();
        params.setAnchorId(R.id.main_bottom_nav);
        params.anchorGravity = Gravity.TOP;
        params.gravity = Gravity.TOP;
        snack.getView().setLayoutParams(params);
        snack.show();
    }

}
