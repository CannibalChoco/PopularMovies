package com.example.android.popularmovies.Utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.android.popularmovies.Movie;
import com.example.android.popularmovies.MovieReview;
import com.example.android.popularmovies.MovieTrailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


class MoviesJsonUtils {

    /* key  for all movie result array in JSON and for review result array*/
    private static final String RESULTS_ARRAY = "results";

    /************************
     * JSON keys for movies *
     ************************/

    /* key  for title string in movie JSON object*/
    private static final String TITLE = "title";

    /* key  for overview string in movie JSON object */
    private static final String OVERVIEW = "overview";

    /* key  for original language string in movie JSON object */
    private static final String ORIGINAL_LANGUAGE = "original_language";

    /* key  for poster path string in movie JSON object */
    private static final String POSTER_PATH = "poster_path";

    /* key  for backdrop path string in movie JSON object */
    private static final String BACKDROP_PATH = "backdrop_path";

    /* key  for the average vote float in movie JSON object */
    private static final String VOTE_AVERAGE = "vote_average";

    /* key  for release date string in movie JSON object*/
    private static final String RELEASE_DATE = "release_date";

    /* key  for id int in movie JSON object*/
    private static final String MOVIE_ID = "id";

    /*************************
     * JSON keys for reviews *
     *************************/

    /* Key for review author*/
    private static final String REVIEW_AUTHOR = "author";

    /* Key for review content*/
    private static final String REVIEW_CONTENT = "content";

    /*************************
     * JSON keys for trailers *
     *************************/

    /* Key for trailers name*/
    private static final String TRAILER_NAME = "name";

    /* Key for trailers key (path to viedo) */
    private static final String TRAILER_KEY = "key";

    /* Key for site where trailer is hosted */
    private static final String TRAILER_SITE = "site";


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
     * Parse the JSON response for review query
     *
     * @param json the JSON response from query
     * @return a List of Movie objects containing reviews
     */
    @Nullable
    public static List<Movie> parseReviewJson (String json){
        if (TextUtils.isEmpty(json)){
            return null;
        }

        List<MovieReview> reviewList = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(json);
            JSONArray resultsArray = root.getJSONArray(RESULTS_ARRAY);

            for (int i = 0, j = resultsArray.length(); i < j; i++){
                MovieReview review = getReviewFromJsonObject(resultsArray.getJSONObject(i));
                reviewList.add(review);
            }

        } catch (JSONException e){
            e.printStackTrace();
        }

        Movie movie = new Movie(reviewList, null);
        List<Movie> list = new ArrayList<>();
        list.add(movie);
        return list;
    }

    /**
     * Parse the JSON response for video query
     *
     * @param json the JSON response from query
     * @return a List of Movie objects containing trailers
     */
    @Nullable
    public static List<Movie> parseTrailerJson (String json){
        if (TextUtils.isEmpty(json)){
            return null;
        }

        List<MovieTrailer> trailerList = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(json);
            JSONArray resultsArray = root.getJSONArray(RESULTS_ARRAY);

            for (int i = 0, j = resultsArray.length(); i < j; i++){
                MovieTrailer trailer = getTrailerFromJsonObject(resultsArray.getJSONObject(i));
                trailerList.add(trailer);
            }

        } catch (JSONException e){
            e.printStackTrace();
        }

        Movie movie = new Movie(null, trailerList);
        List<Movie> list = new ArrayList<>();
        list.add(movie);
        return list;
    }

    /**
     * Helper method to parse a single JSON object review
     *
     * @param trailerJsonObject JSONObject from the root object of JSON response
     * @return Movie
     */
    @NonNull
    private static MovieTrailer getTrailerFromJsonObject (JSONObject trailerJsonObject){
        String name = trailerJsonObject.optString(TRAILER_NAME);
        String key = trailerJsonObject.optString(TRAILER_KEY);
        String site = trailerJsonObject.optString(TRAILER_SITE);

        return new MovieTrailer(name, key, site);
    }

    /**
     * Helper method to parse a single JSON object review
     *
     * @param reviewJsonObject JSONObject from the root object of JSON response
     * @return Movie
     */
    @NonNull
    private static MovieReview getReviewFromJsonObject (JSONObject reviewJsonObject){
        String author = reviewJsonObject.optString(REVIEW_AUTHOR);
        String review = reviewJsonObject.optString(REVIEW_CONTENT);

        //Log.d("REVIEWS", author + "   " + review);
        return new MovieReview(author, review);
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
        String backdropPath = movieJsonObject.optString(BACKDROP_PATH);
        int id = movieJsonObject.optInt(MOVIE_ID, 0);

        return new Movie(title, overview, posterPath, backdropPath, releaseDate, rating, language, id);
    }
}
