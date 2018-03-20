package com.example.android.popularmovies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper {

    /** if the schema is changed, DATABASE_VERSION must be incremented */
    private static final int DATABASE_VERSION = 1;

    /** name of the database file */
    private static final String DATABASE_NAME = "popularMovies.db";

    private static final String CREATE_TABLE = "CREATE TABLE ";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS";

    /**
     * String to create table of movies
     */
    private static final String SQL_CREATE_MOVIE_TABLE =
            CREATE_TABLE +
                    MovieContract.MoviesEntry.TABLE_NAME +
                    " (" +
                    MovieContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MovieContract.MoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                    MovieContract.MoviesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL);";

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}