package com.example.android.popularmovies;

import java.util.List;

/**
 * Created by Emils on 26.02.2018.
 */

public class Movie {

    private String title;
    private String overview;
    private String tagline;
    private String posterPath;
    private String releaseYear;
    private double rating;
    private int runtime;
    private String language;
    private List<String> genre;

    public Movie(String title, String overview, String tagline, String posterPath, String releaseYear,
                 double rating, int runtime, String language, List<String> genre) {
        this.title = title;
        this.overview = overview;
        this.tagline = tagline;
        this.posterPath = posterPath;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.runtime = runtime;
        this.language = language;
        this.genre = genre;
    }

    public String getMovieTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getTagline() {
        return tagline;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public double getRating() {
        return rating;
    }

    public int getRuntime() {
        return runtime;
    }

    public String getLanguage() {
        return language;
    }

    public List<String> getGenre() {
        return genre;
    }

    public int getYearFromDate(String date){
        String year = date.substring(0, 3);
        return Integer.valueOf(year);
    }
}
