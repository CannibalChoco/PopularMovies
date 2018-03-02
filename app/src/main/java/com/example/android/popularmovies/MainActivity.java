package com.example.android.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.GridView;

import com.example.android.popularmovies.Utils.MoviesJsonUtils;
import com.example.android.popularmovies.Utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private String apiKey;
    private List<Movie> movies;
    private RecyclerView gridView;
    private GridLayoutManager layoutManager;

    private String jsonResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiKey = getString(R.string.API_KEY);

        if(isConnected()){
            new MovieQueryTask().execute();
        }

        gridView = findViewById(R.id.gridview);
        layoutManager = new GridLayoutManager(this, 2);
        gridView.setLayoutManager(layoutManager);
    }

    public class MovieQueryTask extends AsyncTask<Void, Void, List<Movie>>{
        @Override
        protected List<Movie> doInBackground(Void... voids) {
            URL url = NetworkUtils.buildUrl(NetworkUtils.PATH_TOP_RATED, apiKey);

            try {
                jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                return MoviesJsonUtils.parseMovieJson(jsonResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> movieList) {
            super.onPostExecute(movieList);

            movies = movieList;
            gridView.setAdapter(new MovieAdapter(MainActivity.this, movieList));
        }
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }
}
