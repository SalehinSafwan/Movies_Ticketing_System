package com.example.desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ShowtimesController {
    @FXML
    private FlowPane showtimesFlowPane;

    @FXML
    public void initialize() {
        loadShowtimes();
    }

    private void loadShowtimes() {
        showtimesFlowPane.getChildren().clear();

        String sql = """
        SELECT m.title, m.poster_path, m.poster_url, st.date, st.time, th.name
        FROM showtimes st
        JOIN movies m ON st.movie_id = m.movie_id
        JOIN theatreid th ON st.theatre_id = th.theatre_id
        WHERE st.status = 1
        ORDER BY st.date, st.time
        """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                VBox card = new VBox(10);
                card.setAlignment(Pos.CENTER);
                card.getStyleClass().add("movie-card");


                ImageView poster = new ImageView();
                poster.setFitWidth(230);
                poster.setFitHeight(280);
                poster.setPreserveRatio(true);

                try {
                    String posterUrl = rs.getString("poster_url");
                    String posterPath = rs.getString("poster_path");
                    if (posterUrl != null && !posterUrl.isEmpty()) {
                        poster.setImage(new Image(posterUrl, true));
                    } else if (posterPath != null && !posterPath.isEmpty()) {
                        poster.setImage(new Image(getClass().getResource(posterPath).toExternalForm()));
                    } else {
                        //show warning
                    }
                } catch (Exception e) {
                    //show error
                }


                Label titleLabel = new Label(rs.getString("title"));
                titleLabel.getStyleClass().add("movie-title");
                titleLabel.setWrapText(true);
                titleLabel.setMaxWidth(230);

                Label infoLabel = new Label("Date: " + rs.getDate("date") +
                        " | Time: " + rs.getTime("time") +
                        " | Theatre: " + rs.getString("name"));
                infoLabel.getStyleClass().add("movie-info");
                infoLabel.setWrapText(true);
                infoLabel.setMaxWidth(230);

                card.getChildren().addAll(poster, titleLabel, infoLabel);
                showtimesFlowPane.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    @FXML void Return(ActionEvent event) {
        go(event, "usermainpage.fxml", 760, 915, "Movies Ticketing System");
    }
}
