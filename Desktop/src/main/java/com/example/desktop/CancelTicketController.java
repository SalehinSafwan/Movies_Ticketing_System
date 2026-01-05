package com.example.desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.time.LocalDate;

public class CancelTicketController {

    @FXML private TextField usernameField, pnrField;
    @FXML private DatePicker datePicker;
    @FXML private Button cancelButton;

    @FXML
    public void initialize() {
        cancelButton.setOnAction(e -> cancelTickets());
    }

    private void cancelTickets() {
        String username = usernameField.getText();
        String pnrText = pnrField.getText();
        LocalDate showDate = datePicker.getValue();

        if (username.isBlank() || pnrText.isBlank() || showDate == null) {
            showAlert(Alert.AlertType.WARNING, "All fields required", "Please fill all the fields");
            return;
        }

        int pnr;
        try {
            pnr = Integer.parseInt(pnrText);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid PNR", "PNR must be a valid integer");
            return;
        }

        final String selectSql = """
            SELECT showtimesid, showtimes_seatid, Price, idTickets
            FROM tickets
            WHERE username=? AND PNR=? AND Status=0
            """;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            int totalPrice = 0;

            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setString(1, username);
                ps.setInt(2, pnr);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int ticketId = rs.getInt("idTickets");
                    int showtimeSeatId = rs.getInt("showtimes_seatid");
                    int price = rs.getInt("price");
                    totalPrice += price;

                    try (PreparedStatement updateTicket = conn.prepareStatement(
                            "UPDATE tickets SET status=1 WHERE idTickets=?")) {
                        updateTicket.setInt(1, ticketId);
                        updateTicket.executeUpdate();
                    }


                    try (PreparedStatement updateSeat = conn.prepareStatement(
                            "UPDATE showtimesid SET status=1 WHERE `showtimes-seat-id`=?")) {
                        updateSeat.setInt(1, showtimeSeatId);
                        updateSeat.executeUpdate();
                    }
                }
            }

            if (totalPrice > 0) {
                // Apply 15% cancellation fee → refund 85%
                int refundAmount = (int) Math.round(totalPrice * 0.85);

                // Reduce revenue
                final String revenueSql = """
                    UPDATE revenue SET `Total Revenue` = `Total Revenue` - ?
                    WHERE date = ?
                    """;
                try (PreparedStatement ps = conn.prepareStatement(revenueSql)) {
                    ps.setInt(1, refundAmount);
                    ps.setDate(2, java.sql.Date.valueOf(showDate));
                    ps.executeUpdate();
                }

                conn.commit();
                showAlert(Alert.AlertType.CONFIRMATION, "Cancellation Successful","Tickets cancelled successfully. Refund Amount: " + refundAmount);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Invalid PNR or username", "No active tickets found for given username and PNR.");
                conn.rollback();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while cancelling tickets.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
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

    @FXML void Home(ActionEvent event) {go(event, "usermainpage.fxml", 760, 915, "Movies Ticketing System");}
}