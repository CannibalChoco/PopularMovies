package com.example.android.popularmovies;


import android.os.Parcel;
import android.os.Parcelable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class Movie implements Parcelable {

    private static final int NO_RATING = -1;
    private static final int NO_ID = -1;

    private final String title;
    private final String overview;
    private final String posterPath;
    private final String backdropPath;
    private final String releaseDate;
    private final double rating;
    private final String language;
    private final int id;
    private List<MovieReview> reviewList;

    public Movie(String title, String overview, String posterPath, String backdropPath, String releaseDate,
                 double rating, String language, int id) {
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.language = language;
        this.id = id;
        this.reviewList = null;
    }

    public Movie (List<MovieReview> reviews){
        this.title = null;
        this.overview = null;
        this.posterPath = null;
        this.backdropPath = null;
        this.releaseDate = null;
        this.rating = NO_RATING;
        this.language = null;
        this.id = NO_ID;
        this.reviewList = reviews;
    }

    private Movie(Parcel in) {
        title = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        backdropPath = in.readString();
        releaseDate = in.readString();
        rating = in.readDouble();
        language = in.readString();
        id = in.readInt();
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

    public String getBackdropPath() {
        return backdropPath;
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
    public List<MovieReview> getReviews(){
        return reviewList;
    }

    public float getRatingForFiveStars(){
        double starRating = (rating / 10) * 5;

        return (float) starRating;
    }

    public String getLanguage() {
        return language;
    }

    public int getId() {
        return id;
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
                            String.valueOf(id) + "; " + "\n" +
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
        dest.writeString(backdropPath);
        dest.writeString(releaseDate);
        dest.writeDouble(rating);
        dest.writeString(language);
        dest.writeInt(id);
    }
}
