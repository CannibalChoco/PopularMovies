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
    private List<String> genres;

    public Movie(String title, String overview, String tagline, String posterPath, String releaseDate,
                 double rating, int runtime, String language, List<String> genres) {
        this.title = title;
        this.overview = overview;
        this.tagline = tagline;
        this.posterPath = posterPath;
        this.releaseYear = getYearFromDate(releaseDate);
        this.rating = rating;
        this.runtime = runtime;
        this.language = language;
        this.genres = genres;
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
        return genres;
    }

    public String getYearFromDate(String date){
        return date.substring(0, 3);
    }
}
