package com.example.android.popularmovies.Utils;

import android.database.Cursor;

import com.example.android.popularmovies.Data.MovieContract;
import com.example.android.popularmovies.Movie;

import java.util.ArrayList;
import java.util.List;

public class DbUtils {

    public static List<Movie> getMovieListFromCursor(Cursor cursor) {
        List<Movie> movieList = new ArrayList<>();

        cursor.moveToFirst();
        for (int i = 0, j = cursor.getCount(); i < j; i++) {
            int id = cursor.getInt(cursor.getColumnIndex(MovieContract.MoviesEntry.COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndex(MovieContract.MoviesEntry.COLUMN_TITLE));
            String year = cursor.getString(cursor.getColumnIndex(MovieContract.MoviesEntry.COLUMN_YEAR));
            double rating = cursor.getFloat(cursor.getColumnIndex(MovieContract.MoviesEntry.COLUMN_RATING));
            String language = cursor.getString(cursor.getColumnIndex(MovieContract.MoviesEntry.COLUMN_LANGUAGE));
            String synopsis = cursor.getString(cursor.getColumnIndex(MovieContract.MoviesEntry.COLUMN_SYNOPSIS));

            Movie movie = new Movie(title, synopsis, "", "", year, rating, language, id);
            movieList.add(movie);
        }

        return movieList;
    }
}
