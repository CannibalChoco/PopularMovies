package com.example.android.popularmovies.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    /**
     * CONTENT_AUTHORITY - the name for the entire content provider
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";

    /**
     * Base of all URI's
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path to movies db table
     */
    public static final String PATH_TABLE_MOVIES = "movies";

    /**
     * Inner class that defines constant values for the movies database table.
     */
    public static abstract class MoviesEntry implements BaseColumns {

        /** The content URI to access the movie data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TABLE_MOVIES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of movies.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TABLE_MOVIES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single movie.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TABLE_MOVIES;

        /** Name of database table for movies */
        public static final String TABLE_NAME = "movies";

        /**
         * Unique ID number for the movie (only for use in the database table).
         *
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * Movie ID
         *
         * Type: INTEGER
         */
        public static final String COLUMN_ID= "movieId";

        /**
         * Movie Title
         *
         * Type: TEXT
         */
        public static final String COLUMN_TITLE= "title";

        /**
         * Movie year
         *
         * Type: INTEGER
         */
        public static final String COLUMN_YEAR= "year";

        /**
         * Movie rating
         *
         * Type: REAL
         */
        public static final String COLUMN_RATING = "rating";

        /**
         * Movie original language
         *
         * Type: TEXT
         */
        public static final String COLUMN_LANGUAGE = "language";

        /**
         * Movie synopsis
         *
         * Type: TEXT
         */
        public static final String COLUMN_SYNOPSIS = "synopsis";

        /**
         * Movie poster path
         *
         * Type: TEXT
         */
        public static final String COLUMN_POSTER_PATH = "posterPath";
    }
}
