package com.example.android.popularmovies;


import android.os.Parcel;
import android.os.Parcelable;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Movie implements Parcelable {

    private final String title;
    private final String overview;
    private final String posterPath;
    private final String releaseDate;
    private final double rating;
    private final String language;

    public Movie(String title, String overview, String posterPath, String releaseDate,
                 double rating, String language) {
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.language = language;
    }

    private Movie(Parcel in) {
        title = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        releaseDate = in.readString();
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
        return title != null ? title : "";
    }

    public String getOverview() {
        return overview;
    }


    public String getPosterPath() {
        return posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getReleaseYear() {
        return releaseDate.substring(0, 4);
    }

    private double getRating() {
        return rating;
    }

    public String getRatingString(){
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.HALF_EVEN);

        return df.format(getRating());
    }

    public float getRatingForFiveStars(){
        double starRating = (rating / 10) * 5;

        return (float) starRating;
    }

    public String getLanguage() {
        return language;
    }

    /**
     * Get all Movie objects fields as a string
     *
     * @return all fields as a string
     */
    public String toString (){
        return title + "; " + "\n" +
                            overview + "; " +  "\n" +
                            posterPath + "; " +  "\n" +
                            releaseDate + "; " +  "\n" +
                            String.valueOf(rating) + "; " + "\n" +
                            language;
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
        dest.writeString(releaseDate);
        dest.writeDouble(rating);
        dest.writeString(language);
    }
}
