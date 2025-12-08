package com.example.desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginpageController {
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Button button, returnbtn;


    @FXML
    void Verify(ActionEvent event) {
        String user = username.getText();
        String pass = password.getText();


        if (user.isEmpty() || pass.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter both username and password");
            return;
        }

        if (verifyLogin(user, pass)) {
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, " + user + "!");
            // ekhane eta kora lagbe go(event, "admindashboard.fxml");
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password");
            password.clear();
        }
    }

    private boolean verifyLogin(String username, String password) {
        String query = "SELECT * FROM admin WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();

             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            return rs.next();

        } catch (Exception e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error",
                     "Could not connect to database.\nMake sure MySQL is running and database exists.");
            return false;
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void go(ActionEvent event, String fxml) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource(fxml));
            javafx.stage.Stage stage = (javafx.stage.Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root, 600,450));
            stage.setTitle("Movies Ticketing System");
            stage.show();
        }
        catch (Exception e) {e.printStackTrace();}
    }

    @FXML void GoBack (ActionEvent event){go(event, "homepage.fxml");}

}
