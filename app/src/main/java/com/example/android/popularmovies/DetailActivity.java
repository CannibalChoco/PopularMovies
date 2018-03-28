package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Data.MovieContract;
import com.example.android.popularmovies.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.ListItemListener,
        ConnectivityReceiver.ConnectivityReceiverListener{

    private static final int ID_TRAILERS = 0;
    private static final int ID_REVIEWS = 1;

    private static final int MOVIE_DB_SEARCH_LOADER_ID = 4;

    private static final String TRAILERS_LIST_KEY = "trailers";

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvOverview) TextView overviewTv;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.ivPoster) ImageView posterIv;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvRating) TextView ratingTv;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvReleaseDate) TextView releaseDateTv;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.ratingBar) RatingBar ratingBar;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvLanguage) TextView languageTv;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.ivBackdrop) ImageView backdropIv;
    @BindView(R.id.trailersPb) ProgressBar trailersPb;
    @BindView(R.id.reviewsPb) ProgressBar reviewsPb;
    @BindView(R.id.rvTrailers) RecyclerView rvTrailers;
    @BindView(R.id.rvReviews) RecyclerView rvReviews;
    @BindView(R.id.tvTrailerEmptyStateText) TextView trailerEmptyStateTextTv;
    @BindView(R.id.tvReviewEmptyStateText) TextView reviewEmptyStateTextTv;
    @BindView(R.id.favoritesBtn) Button btnFavorites;
    @BindView(R.id.noConnectionTv) TextView tvNoConnection;
    @BindView(R.id.labelReviewsTv) TextView labelReviews;
    @BindView(R.id.labelTrailersTv) TextView labelTrailers;

    private boolean isWaitingForInternetConnection = false;
    private boolean hasLoadedTrailers = false;
    private boolean hasLoadedReviews = false;

    private boolean isFavorite = false;

    private Movie movie;
    private int id;
    private List<MovieTrailer> trailers;
    private List<MovieReview> reviews;

    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    private final LoaderManager.LoaderCallbacks moreDataLoaderListener = new
            LoaderManager.LoaderCallbacks<List<Movie>>() {
                @NonNull
                @Override
                public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {

                    switch (id){
                        case MovieLoader.REVIEW_LOADER_ID:
                            return new MovieLoader(DetailActivity.this,
                                    getPathArgsBundle(NetworkUtils.PATH_REVIEWS));
                        case MovieLoader.TRAILER_LOADER_ID:
                            return new MovieLoader(DetailActivity.this,
                                    getPathArgsBundle(NetworkUtils.PATH_TRAILERS));
                        default:
                            throw new RuntimeException("Loader Not Implemented: " + id);
                    }
                }

                @Override
                public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
                    int id = loader.getId();
                    switch (id){
                        case MovieLoader.REVIEW_LOADER_ID:
                            reviewAdapter.clear();
                            if(data != null && !data.isEmpty()){
                                Movie movie = data.get(0);

                                if (movie != null && movie.getReviews() != null){
                                    List<MovieReview> newReviews = movie.getReviews();

                                    if (newReviews != null && !newReviews.isEmpty()){
                                        if (reviews != null){
                                            reviews.clear();
                                        } else {
                                            reviews = new ArrayList<>();
                                        }

                                        reviews.addAll(newReviews);
                                        reviewAdapter.addAll(reviews);
                                        showReviews();
                                    } else {
                                        showReviewEmptyStateText();
                                        reviewEmptyStateTextTv.setText(R.string.reviews_empty_state_text);
                                    }
                                }
                            }

                            break;
                        case MovieLoader.TRAILER_LOADER_ID:
                            trailerAdapter.clear();
                            if(data != null && !data.isEmpty()){
                                Movie movie = data.get(0);

                                if (movie != null && movie.getTrailers() != null){
                                    List<MovieTrailer> newTrailers = movie.getTrailers();

                                    if (newTrailers != null && !newTrailers.isEmpty()){
                                        if (trailers != null){
                                            trailers.clear();
                                        } else {
                                            trailers = new ArrayList<>();
                                        }

                                        trailers.addAll(newTrailers);
                                        trailerAdapter.addAll(trailers);
                                        showTrailers();
                                    } else {
                                        showTrailerEmptyStateText();
                                        trailerEmptyStateTextTv.setText(R.string.trailers_empty_state_text);
                                    }
                                }
                            }

                            break;
                    }
                }

                @Override
                public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
                    int id = loader.getId();
                    switch (id){
                        case MovieLoader.REVIEW_LOADER_ID:
                            reviewAdapter.clear();
                            break;
                        case MovieLoader.TRAILER_LOADER_ID:
                            trailerAdapter.clear();
                    }

                }
            };

    // check if movie is in favorites db
    private final LoaderManager.LoaderCallbacks dbLoaderListener = new
            LoaderManager.LoaderCallbacks<Cursor>() {
                @NonNull
                @Override
                public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
                    String[] projection = new String[]{MovieContract.MoviesEntry.COLUMN_TITLE};
                    String selection = MovieContract.MoviesEntry.COLUMN_TITLE + " = ? ";
                    String[] selectionArgs = new String[]{movie.getMovieTitle()};
                    return new CursorLoader(getApplicationContext(), MovieContract.MoviesEntry.CONTENT_URI,
                            projection,
                            selection,
                            selectionArgs,
                            null);
                }

                @Override
                public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
                    if(data.moveToFirst()) isFavorite = true;
                    btnFavorites.setPressed(isFavorite);
                }

                @Override
                public void onLoaderReset(@NonNull Loader<Cursor> loader) {
                }
            };


    private ConnectivityReceiver connectivityReceiver;

    private AsyncTask dbInsertTask;
    private AsyncTask dbDeleteTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Bundle data = getIntent().getExtras();

        if (data != null && data.containsKey(MainActivity.KEY_MOVIE)){
            movie = data.getParcelable(MainActivity.KEY_MOVIE);

            if(movie != null){
                loadMovieInfoInUi(movie);

                id = movie.getId();
                if (id != 0){
                    LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this,
                            LinearLayoutManager.HORIZONTAL, false);
                    rvTrailers.setLayoutManager(trailerLayoutManager);
                    trailerAdapter = new TrailerAdapter(this,
                            trailers != null ? trailers : new ArrayList<MovieTrailer>(), this);
                    rvTrailers.setAdapter(trailerAdapter);

                    LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this,
                            LinearLayoutManager.VERTICAL, false);
                    rvReviews.setLayoutManager(reviewLayoutManager);

                    reviewAdapter = new ReviewAdapter(
                            reviews != null ? reviews : new ArrayList<MovieReview>());
                    rvReviews.setAdapter(reviewAdapter);

                    getDetailsIfConnected(ID_TRAILERS);
                    getDetailsIfConnected(ID_REVIEWS);
                }
            }
        }

        searchMovieInDb();

        btnFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavorite){
                    dbInsertTask = new DbInsertTask(DetailActivity.this, movie, btnFavorites)
                            .execute();
                    isFavorite = true;
                    Toast.makeText(DetailActivity.this, R.string.msg_favorites_movie_added, Toast.LENGTH_SHORT).show();
                } else {
                    dbDeleteTask = new DbDeleteTask(DetailActivity.this, movie.getMovieTitle(),
                            btnFavorites).execute();
                    isFavorite = false;
                    Toast.makeText(DetailActivity.this, R.string.msg_favorites_movie_removed, Toast.LENGTH_SHORT).show();
                }
                btnFavorites.setPressed(isFavorite);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
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

        if (connectivityReceiver != null){
            this.unregisterReceiver(connectivityReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        if (dbInsertTask != null) dbInsertTask.cancel(true);
        if (dbDeleteTask != null) dbDeleteTask.cancel(true);

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (trailers != null){
            outState.putParcelableArrayList(TRAILERS_LIST_KEY, (ArrayList<MovieTrailer>) trailers);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        trailers = savedInstanceState.getParcelableArrayList(TRAILERS_LIST_KEY);
    }

    @Override
    public void onTrailerClick(int position) {
        String key = trailers.get(position).getKey();

        Uri uri = NetworkUtils.buildUrlForMovieTrailer(key);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        intent.putExtra("force_fullscreen", true);
        startActivity(intent);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected){
            if (isWaitingForInternetConnection){
                if (!hasLoadedTrailers){
                    getDetailsIfConnected(ID_TRAILERS);
                }
                if(!hasLoadedReviews){
                    getDetailsIfConnected(ID_REVIEWS);
                }

                isWaitingForInternetConnection = false;
            }

        } else {
            if (!isWaitingForInternetConnection){
                isWaitingForInternetConnection = true;
            }
        }
    }

    /**
     * AsyncTask to insert a movie into favorites db
     */
    private static class DbInsertTask extends AsyncTask<Void, Void, Uri>{

        private final Movie movie;
        private final WeakReference<Context> context;
        private final WeakReference<Button> favoritesBtn;

        public DbInsertTask (Context context, Movie movie, Button favoritesBtn){
            this.movie = movie;
            this.context = new WeakReference<>(context);
            this.favoritesBtn = new WeakReference<>(favoritesBtn);
        }

        @Override
        protected Uri doInBackground(Void... voids) {
            ContentValues values = new ContentValues();

            values.put(MovieContract.MoviesEntry.COLUMN_ID, movie.getId());
            values.put(MovieContract.MoviesEntry.COLUMN_TITLE, movie.getMovieTitle());
            values.put(MovieContract.MoviesEntry.COLUMN_RATING, movie.getRating());
            values.put(MovieContract.MoviesEntry.COLUMN_SYNOPSIS, movie.getOverview());
            values.put(MovieContract.MoviesEntry.COLUMN_LANGUAGE, movie.getLanguage());
            values.put(MovieContract.MoviesEntry.COLUMN_YEAR, movie.getReleaseYear());
            values.put(MovieContract.MoviesEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            values.put(MovieContract.MoviesEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());

            Context c = context.get();
            return c.getContentResolver().insert(MovieContract.MoviesEntry.CONTENT_URI, values);
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);

            Button button = favoritesBtn.get();

            if (uri != null && button != null){
                button.setPressed(true);
            }
        }
    }

    /**
     * AsyncTask to delete a movie from favorites db
     */
    private static class DbDeleteTask extends AsyncTask<Void, Void, Integer>{

        private final WeakReference<Context> context;
        private final WeakReference<Button> favoritesBtn;
        private final String title;

        public DbDeleteTask(Context context, String title, Button favoritesBtn){
            this.context = new WeakReference<>(context);
            this.favoritesBtn = new WeakReference<>(favoritesBtn);
            this.title = title;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Uri uri = MovieContract.MoviesEntry.CONTENT_URI;

            String selection = MovieContract.MoviesEntry.COLUMN_TITLE + " = ? ";
            String[] selectionArgs = new String[]{title};

            Context c = context.get();

            return c.getContentResolver().delete(uri, selection, selectionArgs);
        }

        @Override
        protected void onPostExecute(Integer rows) {
            Button button = favoritesBtn.get();

            if (rows != 0 && button != null){
                button.setPressed(false);
            }

            super.onPostExecute(rows);
        }
    }

    /**
     * Load the movie details in UI from a  nonull movie object
     *
     * @param movie holds info to be displayed in the UI
     */
    private void loadMovieInfoInUi (Movie movie){
        String overview = movie.getOverview();
        String rating = movie.getRatingString();
        String releaseDate = movie.getReleaseDate();
        String language = movie.getLanguage();
        float ratingForFiveStars = movie.getRatingForFiveStars();

        setTitle(movie.getMovieTitle());

        overviewTv.setText(overview);
        ratingTv.setText(rating);
        releaseDateTv.setText(releaseDate);
        ratingBar.setRating(ratingForFiveStars);
        languageTv.setText(language);

        String posterPath = movie.getPosterPath();
        String backdropPath = movie.getBackdropPath();
        String posterUrl = NetworkUtils.buildUrlForMoviePoster(posterPath);
        String backdropUrl = NetworkUtils.buildUrlForMoviePoster(backdropPath);

        Picasso.with(this).load(posterUrl).into(posterIv);
        Picasso.with(this).load(backdropUrl).into(backdropIv);
    }

    /**
     * Builds Bundle of arguments for loader to load the right data
     *
     * @return Bundle with path for reviews or trailers
     */
    private Bundle getPathArgsBundle (String path){
        Bundle args = new Bundle();
        args.putString(NetworkUtils.PATH_KEY, path);
        args.putInt(NetworkUtils.ID_KEY, id);
        return args;
    }

    /**
     * Preform Review search with LoaderManager initLoader() or restartLoader()
     */
    private void searchReviews() {
        showLoadingReviews();
        LoaderManager loaderManager = getSupportLoaderManager();
        if (loaderManager != null) {
            loaderManager.restartLoader(MovieLoader.REVIEW_LOADER_ID,
                    getPathArgsBundle(NetworkUtils.PATH_REVIEWS), moreDataLoaderListener);
        } else {
            loaderManager.initLoader(MovieLoader.REVIEW_LOADER_ID,
                    getPathArgsBundle(NetworkUtils.PATH_REVIEWS), moreDataLoaderListener);
        }

    }

    /**
     * Preform trailer search with LoaderManager initLoader() or restartLoader()
     */
    private void searchTrailers() {
        showLoadingTrailers();
        LoaderManager loaderManager = getSupportLoaderManager();
        if (loaderManager != null) {
            loaderManager.restartLoader(MovieLoader.TRAILER_LOADER_ID,
                    getPathArgsBundle(NetworkUtils.PATH_TRAILERS), moreDataLoaderListener);
        } else {
            loaderManager.initLoader(MovieLoader.TRAILER_LOADER_ID,
                    getPathArgsBundle(NetworkUtils.PATH_TRAILERS), moreDataLoaderListener);
        }
    }

    /**
     * Preform movie search in favorites db with LoaderManager initLoader() or restartLoader()
     */
    private void searchMovieInDb() {
        LoaderManager loaderManager = getSupportLoaderManager();
        if (loaderManager != null) {
            loaderManager.restartLoader(MOVIE_DB_SEARCH_LOADER_ID,null, dbLoaderListener);
        } else {
            loaderManager.initLoader(MOVIE_DB_SEARCH_LOADER_ID,null, dbLoaderListener);
        }
    }

    /**
     * Sets trailer recyclerVew visible. Called by the trailer loader callback onLoadFinished()
     * when trailers are not null or empty.
     *
     * Sets hasLoadedTrailers to true
     */
    private void showTrailers( ){
        trailerEmptyStateTextTv.setVisibility(GONE);
        rvTrailers.setVisibility(View.VISIBLE);
        trailersPb.setVisibility(GONE);
        labelTrailers.setVisibility(View.VISIBLE);

        hasLoadedTrailers = true;
    }

    /**
     * Sets review recyclerVew visible. Called by the review loader callback onLoadFinished()
     * when reviews are not null or empty.
     *
     * sets hasLoadedReviews to true.
     */
    private void showReviews( ){
       rvReviews.setVisibility(View.VISIBLE);
       reviewEmptyStateTextTv.setVisibility(GONE);
       reviewsPb.setVisibility(GONE);
       labelReviews.setVisibility(View.VISIBLE);

       hasLoadedReviews = true;
    }

    /**
     * Sets emptyStateText when there is a problem loading Trailers.
     * Called by : 1) onLoadFinished() when trailer data is empty or null;
     *             2) getDetailsIfConnected() there is no internet connection
     */
    private void showTrailerEmptyStateText( ){
        trailerEmptyStateTextTv.setVisibility(View.VISIBLE);
        rvTrailers.setVisibility(GONE);
        trailersPb.setVisibility(GONE);
        labelTrailers.setVisibility(View.VISIBLE);

        hasLoadedTrailers = false;
    }

    /**
     * Sets emptyStateText when there is a problem loading reviews.
     * Called by : 1) onLoadFinished() when review data is empty or null;
     *             2) getDetailsIfConnected() there is no internet connection
     */
    private void showReviewEmptyStateText (){
        rvReviews.setVisibility(GONE);
        reviewEmptyStateTextTv.setVisibility(View.VISIBLE);
        reviewsPb.setVisibility(GONE);
        labelReviews.setVisibility(View.VISIBLE);

        hasLoadedReviews = false;
    }

    /**
     * Display only trailer loading indicator, hide all other trailer views
     */
    private void showLoadingTrailers( ){
        trailersPb.setVisibility(View.VISIBLE);
        trailerEmptyStateTextTv.setVisibility(GONE);
        rvTrailers.setVisibility(GONE);
        labelTrailers.setVisibility(View.VISIBLE);
    }

    /**
     * Display only review loading indicator, hide all other review views
     */
    private void showLoadingReviews() {
        rvReviews.setVisibility(GONE);
        reviewEmptyStateTextTv.setVisibility(GONE);
        reviewsPb.setVisibility(View.VISIBLE);
        labelReviews.setVisibility(View.VISIBLE);
    }

    /**
     * Search for trailers and reviews only if there is connectivity, otherwise set emptyStateText
     * @param id detail ID- trailers or reviews
     */
    private void getDetailsIfConnected(int id) {
        if (ConnectivityReceiver.isConnected()) {

            tvNoConnection.setVisibility(View.GONE);

            isWaitingForInternetConnection = false;
            switch (id){
                case ID_TRAILERS:
                    searchTrailers();
                    break;
                case ID_REVIEWS:
                    searchReviews();
                    break;
            }
        } else {
            isWaitingForInternetConnection = true;
            showNoConnectionMessage();
        }
    }

    /**
     * When there is no connectivity, hide all trailers and reviews views and display a single
     * message
     */
    private void showNoConnectionMessage(){
            tvNoConnection.setText(R.string.connectivity_empty_state_text);
            tvNoConnection.setVisibility(View.VISIBLE);

            labelReviews.setVisibility(GONE);
            labelTrailers.setVisibility(GONE);
            rvTrailers.setVisibility(GONE);
            rvReviews.setVisibility(GONE);
            reviewsPb.setVisibility(GONE);
            trailersPb.setVisibility(GONE);
            reviewEmptyStateTextTv.setVisibility(GONE);
            trailerEmptyStateTextTv.setVisibility(GONE);
    }
}
