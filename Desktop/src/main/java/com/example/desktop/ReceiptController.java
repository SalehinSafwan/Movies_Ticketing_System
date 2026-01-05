package com.example.desktop;

import javafx.fxml.FXML;
import javafx.scene.control.Label;


import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class ReceiptController {

    @FXML private Label nameLabel, movieLabel, dateLabel, showtimeLabel, seatsLabel, priceLabel, pnrLabel, purchaseTimeLabel;


    public void setReceiptData(String username, String movie, String date,
                               String showtime, String seats, int totalPrice,
                               int pnr, LocalDateTime purchaseTime) {
        nameLabel.setText(username);
        movieLabel.setText(movie);
        dateLabel.setText(date);
        showtimeLabel.setText(showtime);
        seatsLabel.setText(seats);
        priceLabel.setText(String.valueOf(totalPrice)); priceLabel.setText(priceLabel.getText()+" TAKA");
        pnrLabel.setText(String.valueOf(pnr));
        purchaseTimeLabel.setText(purchaseTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss")));

    }
}
