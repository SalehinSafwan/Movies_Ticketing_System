package com.example.androidapp;

public class Seat {
    public String seatId;
    public int row;
    public int seatNumber;
    public int status; // 0 free, 1 booked
    public int price;

    public Seat() {}

    public Seat(String seatId, int price, int status, int seatNumber, int row) {
        this.seatId = seatId;
        this.price = price;
        this.status = status;
        this.seatNumber = seatNumber;
        this.row = row;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
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

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }
}