package com.example.desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class HomepageController {
    private void go(ActionEvent event, String fxml){
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root, 800,600));
            stage.setTitle("Login page");
            stage.show();
        }
        catch (Exception e) {e.printStackTrace();}
    }
@FXML void OpenLoginPage (ActionEvent event){go(event, "loginpage.fxml");}
}
