package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

// TODO: implemet bottom nav to switch between fragments- overview and reviews
public class DetailActivity extends AppCompatActivity{

    private static final String KEY_REVIEW_LIST = "reviews";

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

    @BindView(R.id.rvTrailers) RecyclerView recyclerViewTrailers;

    private int id;
    private List<MovieReview> reviews;
    private List<MovieTrailer> trailers;

    private TrailerAdapter trailerAdapter;

    private LoaderManager.LoaderCallbacks reviewLoaderListener = new
            LoaderManager.LoaderCallbacks<List<Movie>>() {
        @NonNull
        @Override
        public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
            return new MovieLoader(DetailActivity.this,
                    getPathArgsBundle(NetworkUtils.PATH_REVIEWS));
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
            //Log.i("LOADERS", "REVIEW " + (data != null ? data.toString() : "null"));
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
        }
    };

    private LoaderManager.LoaderCallbacks trailerLoaderListener = new
            LoaderManager.LoaderCallbacks<List<Movie>>() {
                @NonNull
                @Override
                public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
                    return new MovieLoader(DetailActivity.this,
                            getPathArgsBundle(NetworkUtils.PATH_TRAILERS));
                }

                @Override
                public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {

                    trailerAdapter.clear();

                    if(data != null && !data.isEmpty()){
                        Movie movie = data.get(0);
                        List<MovieTrailer> newTrailers = movie.getTrailers();

                        if (trailers != null){
                            trailers.clear();
                            trailerAdapter.addAll(newTrailers);
                        } else {
                            trailers = newTrailers;
                        }

                        trailerAdapter.addAll(trailers);
                    }
                }

                @Override
                public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {

                }
            };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setElevation(10f);
        }

        setTitle(getString(R.string.label_details));

        Bundle data = getIntent().getExtras();

        if (data != null && data.containsKey(MainActivity.KEY_MOVIE)){
            Movie movie = data.getParcelable(MainActivity.KEY_MOVIE);

            if(movie != null){
                loadMovieInfoInUi(movie);

                id = movie.getId();
                if (id != 0){
                    LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this,
                            LinearLayoutManager.HORIZONTAL, false);
                    recyclerViewTrailers.setLayoutManager(trailerLayoutManager);
                    trailerAdapter = new TrailerAdapter(this,
                            trailers != null ? trailers : new ArrayList<MovieTrailer>());
                    recyclerViewTrailers.setAdapter(trailerAdapter);

                    searchReviews();
                    searchTrailers();
                }

            }
        }
    }

    /**
     * Load the movie details in UI from a  nonull movie object
     *
     * @param movie holds info to be displayed in the UI
     */
    private void loadMovieInfoInUi (Movie movie){
        String overview = movie.getOverview();
        String posterPath = movie.getPosterPath();
        String backdropPath = movie.getBackdropPath();
        String posterUrl = NetworkUtils.buildUrlForMoviePoster(posterPath);
        String backdropUrl = NetworkUtils.buildUrlForMoviePoster(backdropPath);
        String rating = movie.getRatingString();
        String releaseDate = movie.getReleaseDate();
        String language = movie.getLanguage();
        float ratingForFiveStars = movie.getRatingForFiveStars();

        overviewTv.setText(overview);
        ratingTv.setText(rating);
        releaseDateTv.setText(releaseDate);
        ratingBar.setRating(ratingForFiveStars);
        languageTv.setText(language);

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
        LoaderManager loaderManager = getSupportLoaderManager();
        if (loaderManager != null) {
            loaderManager.restartLoader(MovieLoader.REVIEW_LOADER_ID,
                    getPathArgsBundle(NetworkUtils.PATH_REVIEWS), reviewLoaderListener);
        } else {
            //noinspection ConstantConditions
            loaderManager.initLoader(MovieLoader.REVIEW_LOADER_ID,
                    getPathArgsBundle(NetworkUtils.PATH_REVIEWS), reviewLoaderListener);
        }
    }

    /**
     * Preform trailer search with LoaderManager initLoader() or restartLoader()
     */
    private void searchTrailers() {
        LoaderManager loaderManager = getSupportLoaderManager();
        if (loaderManager != null) {
            loaderManager.restartLoader(MovieLoader.TRAILER_LOADER_ID,
                    getPathArgsBundle(NetworkUtils.PATH_TRAILERS), trailerLoaderListener);
        } else {
            //noinspection ConstantConditions
            loaderManager.initLoader(MovieLoader.TRAILER_LOADER_ID,
                    getPathArgsBundle(NetworkUtils.PATH_TRAILERS), trailerLoaderListener);
        }
    }

    private void viewTrailer (String key){

    }

}
