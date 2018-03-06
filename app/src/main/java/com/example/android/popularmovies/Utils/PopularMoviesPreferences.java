package com.example.android.popularmovies.Utils;


/**
 * Created by Emils on 06.03.2018.
 */


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

    /**
     *  Default sort order
     */
    public static final String PREFS_SORT_DEFAULT = PREFS_SORT_POPULAR;
}
