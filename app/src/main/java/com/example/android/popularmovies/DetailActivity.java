package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.Data.MovieContract;
import com.example.android.popularmovies.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.ListItemListener,
        ConnectivityReceiver.ConnectivityReceiverListener{

    private static final int ID_TRAILERS = 0;
    private static final int ID_REVIEWS = 1;
    private static final int ID_MOVIE = 2;

    private static final int MOVIE_DB_SEARCH_LOADER_ID = 4;
    private static final int MOVIE_DB_INSERT_LOADER_ID = 5;

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

    private boolean isWaitingForInternetConnection = false;
    private boolean hasLoadedTrailers = false;
    private boolean hasLoadedReviews = false;

    private boolean isFavorite = false;

    private String posterPath;
    private String backdropPath;

    private Movie movie;
    private int id;
    private List<MovieTrailer> trailers;
    private List<MovieReview> reviews;

    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    private LoaderManager.LoaderCallbacks moreDataLoaderListener = new
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
                                    }
                                }
                            }

                            break;
                    }
                }

                @Override
                public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
                }
            };

    // check if movie is in favorites db
    private LoaderManager.LoaderCallbacks dbLoaderListener = new
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        setTitle(getString(R.string.label_details));

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
                    // TODO: trailers are re-queried when user comes back from youtube
                    getDetailsIfConnected(ID_REVIEWS);
                }
            }
        }

        searchMovieInDb();

        // TODO: set state according to movie presence in bd
        btnFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavorite){
                    addToFavorites();
                } else {
                    removeFromFavorites();
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
        Log.i("POSITION", uri.toString());
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
     * Load the movie details in UI from a  nonull movie object
     *
     * @param movie holds info to be displayed in the UI
     */
    private void loadMovieInfoInUi (Movie movie){

//        if (idsInDb.contains(movie.getId())){
//
//        }

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

        posterPath = movie.getPosterPath();
        backdropPath = movie.getBackdropPath();
        String posterUrl = NetworkUtils.buildUrlForMoviePoster(posterPath);
        String backdropUrl = NetworkUtils.buildUrlForMoviePoster(backdropPath);

        Picasso.with(this).load(posterUrl).into(posterIv);
        Picasso.with(this).load(backdropUrl).into(backdropIv);
    }

    /**
     *
     * @return Bundle with path for reviews
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
        showLoadingTrailers();
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

        hasLoadedReviews = false;
    }

    private void showLoadingTrailers( ){
        trailersPb.setVisibility(View.VISIBLE);
        trailerEmptyStateTextTv.setVisibility(GONE);
        rvTrailers.setVisibility(GONE);
    }

    private void showLoadingReviews() {
        rvReviews.setVisibility(GONE);
        reviewEmptyStateTextTv.setVisibility(GONE);
        reviewsPb.setVisibility(View.VISIBLE);
    }

    private void getDetailsIfConnected(int id) {
        if (ConnectivityReceiver.isConnected()) {
            switch (id){
                case ID_TRAILERS:
                    showLoadingTrailers();
                    searchTrailers();
                    break;
                case ID_REVIEWS:
                    showLoadingReviews();
                    searchReviews();
                    break;
                case ID_MOVIE:
                    // additional movie info, like tagline, actors, etc
                    // TODO: showLoading, search, showResults;
                    break;
            }
        } else {
            isWaitingForInternetConnection = true;

            switch (id){
                case ID_TRAILERS:
                    // TODO: adjust message
                    showTrailerEmptyStateText();
                    trailerEmptyStateTextTv.setText("no connection");
                    break;
                case ID_REVIEWS:
                    // TODO: adjust message
                    showReviewEmptyStateText();
                    reviewEmptyStateTextTv.setText("no connection");
                    break;
                case ID_MOVIE:
                    // additional movie info, like tagline, actors, etc
                    // TODO: showEmptyState, adjust message
                    break;
            }

        }
    }

    private void addToFavorites(){
        Thread insert = new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();

                values.put(MovieContract.MoviesEntry.COLUMN_ID, movie.getId());
                values.put(MovieContract.MoviesEntry.COLUMN_TITLE, movie.getMovieTitle());
                values.put(MovieContract.MoviesEntry.COLUMN_RATING, movie.getRatingForFiveStars());
                values.put(MovieContract.MoviesEntry.COLUMN_SYNOPSIS, movie.getOverview());
                values.put(MovieContract.MoviesEntry.COLUMN_LANGUAGE, movie.getLanguage());
                values.put(MovieContract.MoviesEntry.COLUMN_YEAR, movie.getReleaseYear());
                values.put(MovieContract.MoviesEntry.COLUMN_POSTER_PATH, posterPath);
                values.put(MovieContract.MoviesEntry.COLUMN_BACKDROP_PATH, backdropPath);

                Log.i("DATABASE", MovieContract.MoviesEntry.CONTENT_URI.toString());
                Uri uri = getContentResolver().insert(MovieContract.MoviesEntry.CONTENT_URI, values);

                if (uri != null){
                    isFavorite = true;
                }

            }
        });

        insert.start();
    }

    private void removeFromFavorites(){
        Thread delete = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri uri = MovieContract.MoviesEntry.CONTENT_URI;

                String selection = MovieContract.MoviesEntry.COLUMN_TITLE + " = ? ";
                String[] selectionArgs = new String[]{movie.getMovieTitle()};

                int rows = getContentResolver().delete(uri, selection, selectionArgs);

                if (rows != -1){
                    isFavorite = false;
                }

            }
        });

        delete.start();
    }

}
