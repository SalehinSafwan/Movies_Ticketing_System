package com.example.desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminDashboardController {
    private void go(ActionEvent event, String fxml, Integer width, Integer height, String message) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root, width, height));
            stage.setTitle(message);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    @FXML void newAdmin(ActionEvent event) {
        go(event, "newadmin.fxml", 800, 600, "New Admin Registration");
    }

    @FXML void Addmovie(ActionEvent event) {
        go(event, "addmovie.fxml", 1000, 800, "Configuration");
    }

    @FXML void Logout(ActionEvent event) {
        go(event, "loginpage.fxml", 800, 600, "Login Page");
    }
}

