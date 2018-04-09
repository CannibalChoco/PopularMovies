package com.example.android.popularmovies;


import android.os.Parcel;
import android.os.Parcelable;

public class MovieTrailer implements Parcelable{

    private final String name;
    private final String key;
    private final String site;

    public MovieTrailer (String name, String key, String site){
        this.name = name;
        this.key = key;
        this.site = site;
    }

    private MovieTrailer(Parcel in) {
        name = in.readString();
        key = in.readString();
        site = in.readString();
    }

    public static final Creator<MovieTrailer> CREATOR = new Creator<MovieTrailer>() {
        @Override
        public MovieTrailer createFromParcel(Parcel in) {
            return new MovieTrailer(in);
        }

        @Override
        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "MovieTrailer{" +
                "name='" + name + '\'' +
                ", key='" + key + '\'' +
                ", site='" + site + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(key);
        dest.writeString(site);
    }
}
