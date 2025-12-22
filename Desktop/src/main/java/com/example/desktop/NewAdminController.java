package com.example.desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class NewAdminController {
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

    @FXML private TextField username;
    @FXML private PasswordField pass;


    @FXML void Enroll(ActionEvent event) {
        String sql = "Insert into admin (username, password) values (?,?)";
        try(Connection connection = DBConnection.getConnection();
        PreparedStatement prepst = connection.prepareStatement(sql)){

            prepst.setString(1, username.getText());
            prepst.setString(2, pass.getText());

            if(prepst.execute()){
                new Alert(Alert.AlertType.CONFIRMATION, "New Admin Registered Successfully!!").show();
            }
            else{
                new Alert(Alert.AlertType.ERROR, "Registration Failed!!").show();
            }

        } catch (Exception e){
            e.printStackTrace();
            new Alert(Alert.AlertType.WARNING, "Something wrong with Database!!!!").showAndWait();
        }


    }


    @FXML void Back(ActionEvent event) {
        go(event, "admindashboard.fxml", 1300, 900, "Admin Dashboard");
    }
}
