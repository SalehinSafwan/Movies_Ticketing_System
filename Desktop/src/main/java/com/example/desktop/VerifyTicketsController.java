package com.example.desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.*;

public class VerifyTicketsController {

    @FXML private TextField usernameField;
    @FXML private TextField pnrField;
    @FXML private Button verifyButton;
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        verifyButton.setOnAction(e -> verifyTicket());
        backButton.setOnAction(e -> goBack(e));
    }

    private void verifyTicket() {
        String username = usernameField.getText();
        String pnrText = pnrField.getText();

        if (username.isBlank() || pnrText.isBlank()) {
            showAlert(Alert.AlertType.WARNING,"Unfinished Fields","Please fill all fields.");
            return;
        }

        int pnr;
        try {
            pnr = Integer.parseInt(pnrText);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.WARNING,"Invalid Field Input","PNR must be numeric.");
            return;
        }

        final String sql = """
            SELECT status FROM Tickets
            WHERE username=? AND PNR=?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setInt(2, pnr);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int status = rs.getInt("status");
                switch (status) {
                    case 0 -> showAlert(Alert.AlertType.CONFIRMATION,"PERFECT!!","Ticket is valid and confirmed.");
                    case 1 -> showAlert(Alert.AlertType.WARNING,"UH-OH","Ticket was cancelled and is no longer valid.");
                    case 2 -> showAlert(Alert.AlertType.ERROR,"SINCERE APOLOGIES","️Show cancelled by theatre management. Refund will be processed within 3-5 business days.");
                    default -> showAlert(Alert.AlertType.ERROR,"Something wrong with the system","Unknown ticket status.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR,"Invalid Ticket","No ticket found for given username and PNR.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,"System Error","Verification failed. Please try again.");
        }
    }

    private void goBack(ActionEvent event) {
        go(event, "usermainpage.fxml", 760,915, "Movies Ticketing System");
    }

    private void showAlert(Alert.AlertType type, String title ,String msg) {

        Alert alert = new Alert(type, msg);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
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
}