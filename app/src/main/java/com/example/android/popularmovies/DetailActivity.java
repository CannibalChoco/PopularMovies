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

    @BindView(R.id.tvTitle)
    private TextView titleTv;
    @BindView(R.id.tvOverview)
    private TextView overviewTv;
    @BindView(R.id.ivPoster)
    private ImageView posterIv;
    @BindView(R.id.tvRating)
    private TextView ratingTv;
    @BindView(R.id.tvReleaseDate)
    private TextView releaseDateTv;
    @BindView(R.id.ratingBar)
    private RatingBar ratingBar;
    @BindView(R.id.tvLanguage)
    private TextView languageTv;


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
                String title = movie.getMovieTitle();
                String overview = movie.getOverview();
                String posterPath = movie.getPosterPath();
                String posterUrl = NetworkUtils.buildUrlForMoviePoster(posterPath);
                String rating = movie.getRatingString();
                String releaseDate = movie.getReleaseDate();
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
}
