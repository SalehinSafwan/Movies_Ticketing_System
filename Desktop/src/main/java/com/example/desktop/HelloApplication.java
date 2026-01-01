package com.example.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("usermainpage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 760 , 915);
        stage.setTitle("Movies Ticketing System");
        stage.setScene(scene);
        stage.show();
    }
}
