package com.example.android.popularmovies.Utils;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.Movie;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;


public class NetworkUtils {

    /**
     * Timeout constants taken from ABND sample app Soonami
     */
    private static final int URL_READ_TIMEOUT = 10000;
    private static final int URL_CONNECT_TIMEOUT = 15000;
    private static final String URL_REQUEST_METHOD_GET = "GET";

    /**
     * Base Url for querying movie review and video JSON
     */
    private static final String BASE_URL_MOVIES = "https://api.themoviedb.org/3/movie";

    /**
     * Base URL for loading movie posters with the poster size already appended
     */
    private static final String BASE_URL_POSTERS = "https://image.tmdb.org/t/p/w500";

    /**
     * Base URL for opening trailers in youtube
     */
    private static final String BASE_URL_VIEW_TRAILER = "https://youtube.com/watch?v=";

    /**
     * Base URL for trailers thumbnail
     */
    private static final String BASE_URL_TRAILER_THUMBNAIL = "https://img.youtube.com/vi";

    /**
     * Parameter to be appended after movie key to get trailer thumbnail
     */
    private static final String PARAM_TRAILER_THUMBNAIL = "0.jpg";

    private static final String API_KEY = "api_key";

    public static final String PATH_KEY = "path";
    public static final String ID_KEY = "id";

    public static final String PATH_POPULAR = "popular";
    public static final String PATH_TOP_RATED = "top_rated";

    /**
     * BASE_URL_MOVIES appended with /{id}/videos
     *
     * [BASE_URL_MOVIES]/{id}/videos?api_key={key}
     */
    public static final String PATH_TRAILERS = "videos";

    /**
     * BASE_URL_MOVIES appended with /{id}/reviews
     *
     * https://api.themoviedb.org/3/movie/{id}/reviews?api_key={key}
     */
    public static final String PATH_REVIEWS = "reviews";

    public static final int NO_MOVIE_ID = -1;

    /**
     * Fetches Movie data JSON parses it and returns list of movie objects
     *
     * @param path endpoint constants from NetworkUtils class
     * @return a List of Movie
     */
    public static List<Movie> fetchMovieData(String path) {
        URL url = buildUrl(path, NO_MOVIE_ID);
        String jsonResponse = null;
        try {
            jsonResponse = getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return MoviesJsonUtils.parseMovieJson(jsonResponse);
    }

    /**
     * Fetches data by using buildUrl, getResponseFromHttpUrl and parse{...}Json
     *
     * @return a list of movie objects containing reviews or trailers
     */
    public static List<Movie> fetchAdditionalData(int id, String path) {
        URL url = buildUrl(path, id);
        String jsonResponse = null;
        try {
            jsonResponse = getResponseFromHttpUrl(url);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (path.equals(NetworkUtils.PATH_REVIEWS)){
            return MoviesJsonUtils.parseReviewJson(jsonResponse);
        } else {
            return MoviesJsonUtils.parseTrailerJson(jsonResponse);
        }

    }

    /**
     * Builds the URL to query MovieDb
     * Example uri returned :
     * https://api.themoviedb.org/3/movie/top_rated?api_key={key}
     *
     *
     * @param path   the path
     * @param id   movie id
     * @return the URL to use to query the MovieDb for reviews corresponding to the id
     */
    @Nullable
    private static URL buildUrl(String path, int id) {
        Uri uri;

        if (id == NO_MOVIE_ID){
            // get all movie data
            uri = Uri.parse(BASE_URL_MOVIES)
                    .buildUpon()
                    .appendPath(path)
                    .appendQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY).build();
        } else {
            // get data specific to one movie
            uri = Uri.parse(BASE_URL_MOVIES)
                    .buildUpon()
                    .appendPath(String.valueOf(id))
                    .appendPath(path)
                    .appendQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY).build();
        }

        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Build the URL String for loading the movie poster or backdrop
     *
     * @param path poster or backdrop path string returned by the JSON response
     * @return url String for the movie poster or backdrop
     */
    @Nullable
    public static String buildUrlForMoviePoster(String path) {
        Uri uri = Uri.parse(BASE_URL_POSTERS).buildUpon()
                .appendEncodedPath(path).build();

        return uri.toString();
    }

    /**
     * Build the URL String for movie trailer on youtube
     *
     * @param key youtube video key
     * @return url String for the movie trailer
     */
    @Nullable
    public static Uri buildUrlForMovieTrailer(String key) {
        return Uri.parse(BASE_URL_VIEW_TRAILER).buildUpon()
                .appendPath(key).build();
    }

    /**
     * Build the URL String for loading the movie trailer thumbnail
     *
     * @param path thumbnail path string returned by the JSON response
     * @return url String for the movie trailer thumbnail
     */
    @Nullable
    public static String buildUrlForMovieThumbnail(String path) {
        Uri uri = Uri.parse(BASE_URL_TRAILER_THUMBNAIL).buildUpon()
                .appendPath(path).appendPath(PARAM_TRAILER_THUMBNAIL)
                .build();

        return uri.toString();
    }

    /**
     * Returns the entire result from the HTTPS response
     *
     * @param url the url to fetch https response from
     * @return contents of the response
     * @throws IOException when response code is other than 200
     */
    @Nullable
    private static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpsURLConnection urlConnection = null;
        InputStream stream = null;
        String result;

        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setReadTimeout(URL_READ_TIMEOUT);
            urlConnection.setConnectTimeout(URL_CONNECT_TIMEOUT);
            urlConnection.setRequestMethod(URL_REQUEST_METHOD_GET);
            urlConnection.setDoInput(true);
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP Error code: " + responseCode);
            }

            stream = urlConnection.getInputStream();
            Scanner scanner = new Scanner(stream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if (hasInput) {
                result = scanner.next();
            } else {
                return null;
            }

        } finally {
            if (stream != null) {
                stream.close();
            }

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }
}
