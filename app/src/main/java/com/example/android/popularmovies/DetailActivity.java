package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.tvOverview) TextView overviewTv;
    @BindView(R.id.ivPoster) ImageView posterIv;
    @BindView(R.id.tvRating) TextView ratingTv;
    @BindView(R.id.tvReleaseDate) TextView releaseDateTv;
    @BindView(R.id.ratingBar) RatingBar ratingBar;
    @BindView(R.id.tvLanguage) TextView languageTv;
    @BindView(R.id.ivBackdrop) ImageView backdropIv;


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
        }
    }
}
