package com.example.android.popularmovies;


public class MovieTrailer {

    private final String name;
    private final String key;

    public MovieTrailer (String name, String key){
        this.name = name;
        this.key = key;
    }

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
                '}';
    }
}
