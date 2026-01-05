package com.example.desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;


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

    private void go(ActionEvent event, String fxml, Integer width, Integer height, String message) {
        try {
            Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (javafx.stage.Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root, width,height));
            stage.setTitle(message);
            stage.show();
        }
        catch (Exception e) {e.printStackTrace();}
    }

    @FXML void Return (ActionEvent event){
        go(event, "usermainpage.fxml", 760, 915, "Movies Ticketing System");
    }
}
