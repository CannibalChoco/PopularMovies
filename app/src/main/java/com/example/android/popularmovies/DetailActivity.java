package com.example.android.popularmovies;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
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


    // TODO: get movie object from MainActivity
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_detail);

        Bundle data = getIntent().getExtras();
        if (data == null){
            Log.i("INFO", "bundle null");
        }
        Movie movie = data.getParcelable("movie");
        if (movie == null){
            Log.i("INFO", "movie null");
        }
        Log.i("INFO", movie.toString());

        titleTv = findViewById(R.id.tvTitle);
        overviewTv = findViewById(R.id.tvOverview);
        posterIv = findViewById(R.id.ivPoster);

        String title = movie.getMovieTitle();
        String overview = movie.getOverview();
        String posterPath = movie.getPosterPath();
        String posterUrl = NetworkUtils.buildUrlForMoviePoster(posterPath);

        Log.i("INFO", title);
        Log.i("INFO", overview);
        Log.i("INFO", posterPath);
        Log.i("INFO", posterUrl);

        titleTv.setText(title);
        overviewTv.setText(overview);
        Picasso.with(this).load(posterUrl).into(posterIv);

    }
}
