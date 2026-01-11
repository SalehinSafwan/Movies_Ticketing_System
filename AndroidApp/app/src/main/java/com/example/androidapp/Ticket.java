package com.example.androidapp;

public class Ticket {
    public String username;
    public String showtime_id;
    public String showtimes_seat_id;
    public long PNR;
    public int price;
    public int status;

    public Ticket() {}

    public Ticket(String username, int status, int price, long PNR, String showtimes_seat_id, String showtime_id) {
        this.username = username;
        this.status = status;
        this.price = price;
        this.PNR = PNR;
        this.showtimes_seat_id = showtimes_seat_id;
        this.showtime_id = showtime_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getShowtime_id() {
        return showtime_id;
    }

    public void setShowtime_id(String showtime_id) {
        this.showtime_id = showtime_id;
    }

    public String getShowtimes_seat_id() {
        return showtimes_seat_id;
    }

    public void setShowtimes_seat_id(String showtimes_seat_id) {
        this.showtimes_seat_id = showtimes_seat_id;
    }



    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
