package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieReview implements Parcelable{

    private final String author;
    private final String review;

    public MovieReview (String author, String review){
        this.author = author;
        this.review = review;
    }

    private MovieReview(Parcel in) {
        author = in.readString();
        review = in.readString();
    }

    public static final Creator<MovieReview> CREATOR = new Creator<MovieReview>() {
        @Override
        public MovieReview createFromParcel(Parcel in) {
            return new MovieReview(in);
        }

        @Override
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public String getReview() {
        return review;
    }

    public String toString (){
        return review + "\n" + author;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(review);
    }
}
