package com.example.androidapp;

public class Movie {
    public String title;
    public String release_date;
    public String genre;
    public String duration;
    public String posterUrl;

    public Movie() {} // Firebase requires empty constructor

    public Movie(String title, String release_date, String genre, String duration, String posterUrl) {
        this.title = title;
        this.release_date = release_date;
        this.genre = genre;
        this.duration = duration;
        this.posterUrl = posterUrl;
    }
}
