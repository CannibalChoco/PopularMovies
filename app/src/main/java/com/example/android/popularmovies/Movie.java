package com.example.android.popularmovies;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Emils on 26.02.2018.
 */

public class Movie implements Parcelable {

    private String title;
    private String overview;
    private String posterPath;
    private String releaseYear;
    private double rating;
    private String language;

    public Movie(String title, String overview, String posterPath, String releaseDate,
                 double rating, String language) {
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseYear = getYearFromDate(releaseDate);
        this.rating = rating;
        this.language = language;
    }

    protected Movie(Parcel in) {
        title = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        releaseYear = in.readString();
        rating = in.readDouble();
        language = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getMovieTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
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

    public String getLanguage() {
        return language;
    }

    public String getYearFromDate(String date){
        return date.substring(0, 4);
    }

    /**
     * Get all Movie objects fields as a string
     *
     * @return all fields as a string
     */
    public String toString (){
        String movieString = title + "; " + "\n" +
                            overview + "; " +  "\n" +
                            posterPath + "; " +  "\n" +
                            releaseYear + "; " +  "\n" +
                            String.valueOf(rating) + "; " + "\n" +
                            language;

        return movieString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(posterPath);
        dest.writeString(releaseYear);
        dest.writeDouble(rating);
        dest.writeString(language);
    }
}
