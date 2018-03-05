package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by Emils on 26.02.2018.
 */

public class DetailActivity extends AppCompatActivity {

    private TextView titleTv;
    private TextView overviewTv;
    private ImageView posterIv;
    private TextView ratingTv;
    private TextView releaseDateTv;
    private RatingBar ratingBar;
    private TextView languageTv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle(getString(R.string.label_details));

        Bundle data = getIntent().getExtras();
        Movie movie = data.getParcelable("movie");

        Log.i("INFO", movie.toString());

        titleTv = findViewById(R.id.tvTitle);
        overviewTv = findViewById(R.id.tvOverview);
        posterIv = findViewById(R.id.ivPoster);
        ratingTv = findViewById(R.id.tvRating);
        releaseDateTv = findViewById(R.id.tvReleaseDate);
        ratingBar = findViewById(R.id.ratingBar);
        languageTv = findViewById(R.id.tvLanguage);

        String title = movie.getMovieTitle();
        String overview = movie.getOverview();
        String posterPath = movie.getPosterPath();
        String posterUrl = NetworkUtils.buildUrlForMoviePoster(posterPath);
        String rating = movie.getRatingString();
        String releaseDate = movie.getReleaseYear();
        String language = movie.getLanguage();

        float ratingFloat = Float.valueOf(rating);

        titleTv.setText(title);
        overviewTv.setText(overview);
        ratingTv.setText(rating);
        releaseDateTv.setText(releaseDate);
        ratingBar.setRating(ratingFloat);
        languageTv.setText(language);

        Picasso.with(this).load(posterUrl).into(posterIv);

    }
}
