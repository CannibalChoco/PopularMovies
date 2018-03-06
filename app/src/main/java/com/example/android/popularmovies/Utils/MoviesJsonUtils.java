package com.example.android.popularmovies.Utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

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

    /* key  for all movie result array in JSON */
    private static final String RESULTS_ARRAY = "results";

    /* key  for title string in movie JSON object*/
    private static final String TITLE = "title";

    /* key  for overview string in movie JSON object */
    private static final String OVERVIEW = "overview";

    /* key  for original language string in movie JSON object */
    private static final String ORIGINAL_LANGUAGE = "original_language";

    /* key  for poster path string in movie JSON object */
    private static final String POSTER_PATH = "poster_path";

    /* key  for the average vote float in movie JSON object */
    private static final String VOTE_AVERAGE = "vote_average";

    /* key  for release date string in movie JSON object*/
    private static final String RELEASE_DATE = "release_date";

    /**
     * Parse the JSON response received when query returns multiple movies
     *
     * @param json json the JSON response from query
     * @return a List of Movie objects
     */
    @Nullable
    public static List<Movie> parseMovieJson (String json){
        if (TextUtils.isEmpty(json)){
            return null;
        }

        List<Movie> movieList = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(json);
            JSONArray resultsArray = root.getJSONArray(RESULTS_ARRAY);

            for (int i = 0, j = resultsArray.length(); i < j; i++){
                Movie movie = getMovieFromJsonObject(resultsArray.getJSONObject(i));
                movieList.add(movie);
            }

        } catch (JSONException e){
            e.printStackTrace();
        }

        return movieList;
    }

    /**
     * Helper method to parse a single JSON object movie
     *
     * @param movieJsonObject JSONObject from the root object of JSON response
     * @return Movie
     */
    @NonNull
    private static Movie getMovieFromJsonObject (JSONObject movieJsonObject){
        String title = movieJsonObject.optString(TITLE);
        String overview = movieJsonObject.optString(OVERVIEW);
        String language = movieJsonObject.optString(ORIGINAL_LANGUAGE);
        String posterPath = movieJsonObject.optString(POSTER_PATH);
        double rating = movieJsonObject.optDouble(VOTE_AVERAGE);
        String releaseDate = movieJsonObject.optString(RELEASE_DATE);

        return new Movie(title, overview, posterPath, releaseDate, rating, language);
    }

    /**
     * Parse JSON returned for single movie to get additional details about the movie
     *
     * @param json the JSON response from query
     * @return a single Movie object
     */
    @Nullable
    public static Movie parseSingleMovieJson(String json){

        // JSON keys
        final String TITLE = "original_title";
        final String LANGUAGE = "original_language";
        final String OVERVIEW = "overview";
//        final String TAGLINE = "tagline";
        final String POSTER_PATH = "poster_path";
        final String RELEASE_DATE = "release_date";
        final String RATING = "vote_average";
//        final String RUNTIME = "runtime";
//        // genres array
//        final String GENRES = "genres";
//        final String GENRES_NAME = "name";

        try {
            JSONObject rootObject = new JSONObject(json);

            String title = rootObject.optString(TITLE);
            String overview = rootObject.optString(OVERVIEW);
            String language = rootObject.optString(LANGUAGE);
            //String tagline = rootObject.optString(TAGLINE);
            String posterPath = rootObject.optString(POSTER_PATH);
            String releaseDate = rootObject.optString(RELEASE_DATE);
            double rating = rootObject.optDouble(RATING);
            //int runtime = rootObject.optInt(RUNTIME);

//            JSONArray genresArray = rootObject.optJSONArray(GENRES);
//            List<String> genresList = new ArrayList<>();
//
//            for (int i = 0, j = genresArray.length(); i < j; i ++){
//                JSONObject genreObject = genresArray.optJSONObject(i);
//                genresList.add(genreObject.optString(GENRES_NAME));
//            }

            return new Movie(title, overview, posterPath, releaseDate, rating,
                            language);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
