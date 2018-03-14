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
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity implements
        android.support.v4.app.LoaderManager.LoaderCallbacks<List<Movie>>{

    private static final int REVIEW_LOADER_ID = 2;

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

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.rvReviews)RecyclerView reviewRecyclerView;

    private int id;
    private ReviewAdapter reviewAdapter;
    private List<MovieReview> reviewList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setElevation(10f);
        }

        reviewList = new ArrayList<>();

        setTitle(getString(R.string.label_details));

        Bundle data = getIntent().getExtras();

        if (data != null && data.containsKey(MainActivity.KEY_MOVIE)){
            Movie movie = data.getParcelable(MainActivity.KEY_MOVIE);

            if(movie != null){
                loadMovieInfoInUi(movie);

                id = movie.getId();
                if (id != 0){
                    Log.d("REVIEWS", "init layout");
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                            LinearLayoutManager.HORIZONTAL, false);
                    reviewRecyclerView.setLayoutManager(layoutManager);
                    reviewAdapter = new ReviewAdapter(reviewList);
                    reviewRecyclerView.setAdapter(reviewAdapter);
                    searchReviews();
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

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
        return new MovieLoader(this, getPathArgsBundle());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
        reviewAdapter.clear();

        if (data != null && !data.isEmpty()) {
            if (reviewList != null) {
                reviewList.clear();
                reviewList.addAll(data.get(0).getReviews());
            } else {
                reviewList = data.get(0).getReviews();
            }

            reviewAdapter.addAll(reviewList);

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
        reviewAdapter.clear();
    }

    /**
     *
     * @return Bundle with path for reviews
     */
    private Bundle getPathArgsBundle (){
        Bundle args = new Bundle();
        args.putString(NetworkUtils.PATH_KEY, NetworkUtils.PATH_REVIEWS);
        args.putInt(NetworkUtils.ID_KEY, id);
        return args;
    }

    /**
     * Preform Review search with LoaderManager initLoader() or restartLoader()
     */
    private void searchReviews() {
        LoaderManager loaderManager = getSupportLoaderManager();
        if (loaderManager != null) {
            loaderManager.restartLoader(REVIEW_LOADER_ID, getPathArgsBundle(), this);
        } else {
            //noinspection ConstantConditions
            loaderManager.initLoader(REVIEW_LOADER_ID, getPathArgsBundle(), this);
        }
    }
}
