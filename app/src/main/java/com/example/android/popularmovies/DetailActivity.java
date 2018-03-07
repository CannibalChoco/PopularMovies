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


public class DetailActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setElevation(10f);
        }

        setTitle(getString(R.string.label_details));

        Bundle data = getIntent().getExtras();

        if (data != null && data.containsKey("movie")){
            Movie movie = data.getParcelable("movie");

            TextView titleTv = findViewById(R.id.tvTitle);
            TextView overviewTv = findViewById(R.id.tvOverview);
            ImageView posterIv = findViewById(R.id.ivPoster);
            TextView ratingTv = findViewById(R.id.tvRating);
            TextView releaseDateTv = findViewById(R.id.tvReleaseDate);
            RatingBar ratingBar = findViewById(R.id.ratingBar);
            TextView languageTv = findViewById(R.id.tvLanguage);

            String title = movie.getMovieTitle();
            String overview = movie.getOverview();
            String posterPath = movie.getPosterPath();
            String posterUrl = NetworkUtils.buildUrlForMoviePoster(posterPath);
            String rating = movie.getRatingString();
            String releaseDate = movie.getReleaseYear();
            String language = movie.getLanguage();
            float ratingForFiveStars = movie.getRatingForFiveStars();

            titleTv.setText(title);
            overviewTv.setText(overview);
            ratingTv.setText(rating);
            releaseDateTv.setText(releaseDate);
            ratingBar.setRating(ratingForFiveStars);
            languageTv.setText(language);

            Picasso.with(this).load(posterUrl).into(posterIv);
        }
    }
}
