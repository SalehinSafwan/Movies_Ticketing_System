package com.example.desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserMainPageController {

    private void go(ActionEvent event, String fxml, Integer width, Integer height, String message) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource(fxml));
            javafx.stage.Stage stage = (javafx.stage.Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root, width,height));
            stage.setTitle(message);
            stage.show();
        }
        catch (Exception e) {e.printStackTrace();}
    }


    @FXML private TilePane firstpagetilepane;
    @FXML void initialize(){
        loadMovies();
    }

    private void loadMovies() {
        firstpagetilepane.getChildren().clear();

        String sql = "SELECT movie_id, title, genre, duration, release_date, poster_path, poster_url FROM movies";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                VBox card = createMovieCard(
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getString("duration"),
                        rs.getDate("release_date").toString(),
                        rs.getString("poster_path"),
                        rs.getString("poster_url")
                );
                firstpagetilepane.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private VBox createMovieCard(String title, String genre, String duration, String releaseDate, String posterPath, String posterUrl) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(330,380);
        card.setStyle("-fx-border-radius: 10; -fx-border-color: #1c302d; -fx-border-width: 2; -fx-padding: 10;");


        ImageView poster = new ImageView();
        poster.setFitWidth(276);
        poster.setFitHeight(410);
        poster.setPreserveRatio(true);

        try {
            if (posterUrl != null && !posterUrl.isEmpty()) {
                poster.setImage(new Image(posterUrl, true));
            } else if (posterPath != null && !posterPath.isEmpty()) {
                poster.setImage(new Image(getClass().getResource(posterPath).toExternalForm()));
                
            } else {
                // HOYnai alert dekhao
            }
        } catch (Exception e) {
           //Again, hoynai!!
        }


        Label titleLabel = new Label(title);
        Label genreLabel = new Label(genre);
        Label infoLabel = new Label(duration + " | Release: " + releaseDate);

        card.getChildren().addAll(poster, titleLabel, genreLabel, infoLabel);
        return card;

    }


    @FXML void login(ActionEvent event) {go(event, "loginpage.fxml", 800, 600, "ADMINISTRATION LOGIN");}
}
