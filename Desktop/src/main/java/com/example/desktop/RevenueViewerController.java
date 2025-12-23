package com.example.desktop;

import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.xdevapi.PreparableStatement;
import com.mysql.cj.xdevapi.Table;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class RevenueViewerController {
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

    @FXML private TableView<RevenueModel> tableView;
    @FXML private TableColumn<RevenueModel, String> dateColumn;
    @FXML private TableColumn<RevenueModel, Double> revenueColumn;
    @FXML private TableColumn<RevenueModel, Double> lossColumn;

    @FXML private ObservableList<RevenueModel> revenuelist = FXCollections.observableList(FXCollections.observableArrayList());

    @FXML private void initialize (){
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        revenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        lossColumn.setCellValueFactory(new PropertyValueFactory<>("cancellationloss"));

       // loadRevenueData();
    }

//    private void loadRevenueData() {
//        String sql = "Select date, total revenue, cancellationloss FROM revenue";
//
//        try(Connection connection = DBConnection.getConnection();
//            PreparableStatement st = connection.prepareStatement(sql);
//        ){
//            // Data niye asha
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }
}
