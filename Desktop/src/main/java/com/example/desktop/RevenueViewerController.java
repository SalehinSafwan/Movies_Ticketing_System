package com.example.desktop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
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
    @FXML private DatePicker datePicker;
    @FXML private RadioButton upto, from;

    @FXML private TextField minrevenue,maxrevenue;
    @FXML private Button filterButton, ResetButton;

    @FXML private final ObservableList<RevenueModel> revenuelist = FXCollections.observableArrayList();

    @FXML private void initialize (){
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        revenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        lossColumn.setCellValueFactory(new PropertyValueFactory<>("cancellationloss"));

        loadRevenueData();




        filterButton.setOnAction(event -> applyFilters());
        ResetButton.setOnAction(event -> resetFilters());

        minrevenue.setTextFormatter(new TextFormatter<>(change -> {
            if(change.getText().matches("[0-9]*")) return change;
            return null;
        }));
        maxrevenue.setTextFormatter(new TextFormatter<>(change -> {
            if(change.getText().matches("[0-9]*")) return change;
            return null;
        }));
    }

    private void resetFilters() {
        datePicker.setValue(null);
        upto.setSelected(false);
        from.setSelected(false);
        minrevenue.clear();
        maxrevenue.clear();
        revenuelist.clear();

        loadRevenueData();
    }

    private void applyFilters() {
        revenuelist.clear();

        StringBuilder sql = new StringBuilder("SELECT `Date`, `Total Revenue`, `Cancellation Loss` FROM revenue WHERE 1=1 ");
        if (datePicker.getValue() != null) {
            Date selectedDate = Date.valueOf(datePicker.getValue());
            if (upto.isSelected()) {
                from.setSelected(false);
                sql.append("AND `Date` <= '").append(selectedDate).append("' ");
            } else if (from.isSelected()) {
                sql.append("AND `Date` >= '").append(selectedDate).append("' ");
            }
        }

        try {
            if (!minrevenue.getText().isBlank()) {
                int min = Integer.parseInt(minrevenue.getText());
                sql.append("AND `Total Revenue` >= ").append(min).append(" ");
            }
            if (!maxrevenue.getText().isBlank()) {
                int max = Integer.parseInt(maxrevenue.getText());
                sql.append("AND `Total Revenue` <= ").append(max).append(" ");
            }
        } catch (NumberFormatException e) {
            //showAlert;
        }

        try (Connection connection = DBConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql.toString())) {
            while (rs.next()) {
                String date = rs.getDate("Date").toString();
                Integer total = rs.getInt("Total Revenue");
                Integer loss = rs.getInt("Cancellation Loss");
                revenuelist.add(new RevenueModel(date, total, loss));
            }
            tableView.setItems(revenuelist);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
