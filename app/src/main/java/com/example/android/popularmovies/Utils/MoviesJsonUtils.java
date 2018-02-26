package com.example.android.popularmovies.Utils;

import com.example.android.popularmovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emils on 16.02.2018.
 */

public class MoviesJsonUtils {

    public static Movie parseMovieJson(String json){

        // JSON keys
        final String TITLE = "original_title";
        final String LANGUAGE = "original_language";
        final String OVERVIEW = "overview";
        final String TAGLINE = "tagline";
        final String POSTER_PATH = "poster_path";
        final String RELEASE_DATE = "release_date";
        final String RATING = "vote_average";
        final String RUNTIME = "runtime";
        // genres array
        final String GENRES = "genres";
        final String GENRES_NAME = "name";

        try {
            JSONObject rootObject = new JSONObject(json);

            String title = rootObject.optString(TITLE);
            String overview = rootObject.optString(OVERVIEW);
            String language = rootObject.optString(LANGUAGE);
            String tagline = rootObject.optString(TAGLINE);
            String posterPath = rootObject.optString(POSTER_PATH);
            String releaseDate = rootObject.optString(RELEASE_DATE);
            double rating = rootObject.optDouble(RATING);
            int runtime = rootObject.optInt(RUNTIME);

            JSONArray genresArray = rootObject.optJSONArray(GENRES);
            List<String> genresList = new ArrayList<>();

            for (int i = 0, j = genresArray.length(); i < j; i ++){
                // TODO get each genre and store it in to the list
            }

            return new Movie();

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
