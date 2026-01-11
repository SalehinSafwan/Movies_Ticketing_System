package com.example.androidapp;

public class Showtime {
    public String movieTitle;
    public String posterUrl;
    public String date;
    public String time;
    public String theatreName;

    public Showtime() {}

    public Showtime(String movieTitle, String theatreName, String time, String date, String posterUrl) {
        this.movieTitle = movieTitle;
        this.theatreName = theatreName;
        this.time = time;
        this.date = date;
        this.posterUrl = posterUrl;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTheatreName() {
        return theatreName;
    }

    public void setTheatreName(String theatreName) {
        this.theatreName = theatreName;
    }
}