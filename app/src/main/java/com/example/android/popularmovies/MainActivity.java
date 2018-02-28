package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.popularmovies.Utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity{

    private String apiKey;

    private TextView sampleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiKey = getString(R.string.API_KEY);

        sampleText = findViewById(R.id.sampleText);

        new MovieQueryTask().execute();
    }

    public class MovieQueryTask extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... voids) {
            URL url = NetworkUtils.buildUrl(NetworkUtils.PATH_TOP_RATED, apiKey);
            String jsonResponse = "";
            try {
                jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null && !s.equals("")) {
                sampleText.setText(s);
            }
        }
    }
}
