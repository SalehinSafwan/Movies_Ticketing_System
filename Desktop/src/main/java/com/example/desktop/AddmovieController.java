package com.example.desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddmovieController {
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

    @FXML private TextField title, duration, genreText;
    @FXML private DatePicker releaseDate;
    @FXML private ComboBox<String> genre;
    @FXML private Button addGenre;


    @FXML public void initialize() {
        genre.getItems().clear();
        addGenre.setOnAction(event -> {
            String newItem = genreText.getText().trim();
            if(!newItem.isEmpty() && !genre.getItems().contains(newItem)) {
                genre.getItems().add(newItem);
                genre.setValue(newItem);
                genreText.clear();
            }
        });
        duration.setTextFormatter(new TextFormatter<>(change -> {
            if(change.getText().matches("[0-9]*")) return change;
            return null;
        }));
    }

    @FXML void AddMovie(ActionEvent event) {

        String sql = "INSERT INTO Movies (title, genre, release_date, duration) VALUES (?,?,?,?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1,title.getText());
            preparedStatement.setString(2, genre.getValue());
            preparedStatement.setDate(3, java.sql.Date.valueOf(releaseDate.getValue()));
            preparedStatement.setString(4, duration.getText()+"  Minutes");

            if(preparedStatement.executeUpdate() > 0){
                new Alert(Alert.AlertType.CONFIRMATION, "Movie ADDED Successfully!!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to add movie!").show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.WARNING, "Something wrong with Database!!!!").showAndWait();
        }

        title.clear();
        duration.clear();
        releaseDate.setValue(null);
        genre.setValue(null);
    }


    @FXML void backDashboard(ActionEvent event) {
        go(event, "admindashboard.fxml", 900, 700, "Admin Dashboard");
    }
}
