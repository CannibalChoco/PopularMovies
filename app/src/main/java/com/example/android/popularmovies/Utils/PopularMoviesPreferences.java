package com.example.android.popularmovies.Utils;


/**
 * Class for defining shared preference constants
 */
public class PopularMoviesPreferences {

    public static final String PREFS_POPULAR_MOVIES = "moviePreferences";

    /**
     * key for sort order shared preference
     */
    public static final String PREFS_SORT_ORDER = "sortOrder";

    /**
     * possible value of sort order shared preference
     */
    public static final String PREFS_SORT_POPULAR = "popular";
    public static final String PREFS_SORT_RATINGS = "ratings";
    public static final String PREFS_SORT_FAVORITES = "favorites";

    /**
     *  Default sort order
     */
    public static final String PREFS_SORT_DEFAULT = PREFS_SORT_POPULAR;
}
