package com.example.desktop;

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
import java.sql.Date;
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
    @FXML private TableColumn<RevenueModel, Integer> revenueColumn;
    @FXML private TableColumn<RevenueModel, Integer> lossColumn;

    @FXML private final ObservableList<RevenueModel> revenuelist = FXCollections.observableArrayList();

    @FXML private void initialize (){
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        revenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        lossColumn.setCellValueFactory(new PropertyValueFactory<>("cancellationloss"));

        loadRevenueData();
    }

    private void loadRevenueData() {
        String sql = "Select `Date`, `Total Revenue`, `Cancellation Loss` FROM revenue";

        try(Connection connection = DBConnection.getConnection();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String date = rs.getDate("Date").toString();
                Integer total = rs.getInt("Total Revenue");
                Integer loss = rs.getInt("Cancellation Loss");

                revenuelist.add(new RevenueModel(date, total, loss));
            }
            tableView.setItems(revenuelist);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML void Back(ActionEvent event) {go(event, "admindashboard.fxml", 1300, 900, "Admin Dashboard");}
}
