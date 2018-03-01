package com.example.android.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.popularmovies.Utils.MoviesJsonUtils;
import com.example.android.popularmovies.Utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private String apiKey;

    private TextView sampleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiKey = getString(R.string.API_KEY);

        sampleText = findViewById(R.id.sampleText);

        if(isConnected()){
            new MovieQueryTask().execute();
        } else {
            sampleText.setText("No network connection");
        }

    }

    public class MovieQueryTask extends AsyncTask<Void, Void, List<Movie>>{
        @Override
        protected List<Movie> doInBackground(Void... voids) {
            URL url = NetworkUtils.buildUrl(NetworkUtils.PATH_TOP_RATED, apiKey);

            List<Movie> movieList = null;
            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                movieList = MoviesJsonUtils.parseMovieJson(jsonResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movieList;
        }

        @Override
        protected void onPostExecute(List<Movie> movieList) {
            super.onPostExecute(movieList);

            if (movieList.size() > 0) {
                String moviesInfo = "";
                for (int i = 0, j = movieList.size(); i < j; i++){
                    moviesInfo += movieList.get(i).toString() + "\n";
                }
                sampleText.setText(moviesInfo);
            }
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
