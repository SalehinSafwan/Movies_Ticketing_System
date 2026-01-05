package com.example.desktop;


import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class BuyTicketsController extends ReceiptController{

    public class BookingRow {
        private final StringProperty seat = new SimpleStringProperty();
        private final IntegerProperty price = new SimpleIntegerProperty();

        public BookingRow(String seatLabel, int price) {
            this.seat.set(seatLabel);
            this.price.set(price);
        }

        public StringProperty seatProperty() { return seat; }
        public IntegerProperty priceProperty() { return price; }

        public String getSeat() { return seat.get(); }
        public int getPrice() { return price.get(); }
    }


    @FXML private TextField usernameField;
    @FXML private ComboBox<String> movieCombo,showtimeCombo;
    @FXML private DatePicker datePicker;
    @FXML private Label theatreLabel, totalPriceLabel;
    @FXML private GridPane seatGrid;
    @FXML private TableView<BookingRow> bookingTable;
    @FXML private TableColumn<BookingRow, String> seatColumn;
    @FXML private TableColumn<BookingRow, Integer> priceColumn;
    @FXML private Button buyButton;


    private final Map<String, Integer> timeToShowtimeId = new HashMap<>();
    private final Map<String, String> timeToTheatreName = new HashMap<>();
    private final Map<String, Integer> timeToTheatreId = new HashMap<>();
    private final Map<String, Integer> timeToTheatrePrice = new HashMap<>();

    private int selectedShowtimeId = -1;
    private int selectedTheatreId = -1;
    private int currentSeatPrice = 0;


    private final List<Integer> selectedShowtimeSeatIds = new ArrayList<>();

    private int totalPrice = 0;

    @FXML
    public void initialize() {
        configureDatePicker();
        configureBookingTable();
        loadMovies();

        movieCombo.valueProperty().addListener((obs, o, n) -> loadShowtimesForSelection());
        datePicker.valueProperty().addListener((obs, o, n) -> loadShowtimesForSelection());
        showtimeCombo.valueProperty().addListener((obs, o, n) -> handleShowtimeSelection(n));

        buyButton.setOnAction(event ->  confirmPurchase(event));
    }

    private void configureDatePicker() {
        datePicker.setValue(LocalDate.now());
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.isBefore(today) || date.isAfter(today.plusDays(1)));
            }
        });
    }

    private void configureBookingTable() {
        seatColumn.setCellValueFactory(data -> data.getValue().seatProperty());
        priceColumn.setCellValueFactory(data -> data.getValue().priceProperty().asObject());
        resetBookingSummary();
    }

    private void resetBookingSummary() {
        bookingTable.getItems().clear();
        selectedShowtimeSeatIds.clear();
        totalPrice = 0;
        totalPriceLabel.setText("Total: 0");
    }

    private void loadMovies() {
        movieCombo.getItems().clear();
        final String sql = "SELECT title FROM Movies ORDER BY title";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                movieCombo.getItems().add(rs.getString("title"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadShowtimesForSelection() {
        showtimeCombo.getItems().clear();
        timeToShowtimeId.clear();
        timeToTheatreName.clear();
        timeToTheatreId.clear();
        timeToTheatrePrice.clear();
        theatreLabel.setText("Theatre: ");

        String movieTitle = movieCombo.getValue();
        LocalDate date = datePicker.getValue();
        if (movieTitle == null || date == null) return;


        final String sql = """
            SELECT st.showtime_id, st.time, th.theatre_id, th.name, th.price
            FROM showtimes st
            JOIN movies m ON st.movie_id = m.movie_id
            JOIN theatreid th ON st.theatre_id = th.theatre_id
            WHERE m.title = ? AND st.date = ? AND st.status = 1
            ORDER BY st.time
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, movieTitle);
            ps.setDate(2, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int showtimeId = rs.getInt("showtime_id");
                    String timeText = rs.getTime("time").toLocalTime().toString().substring(0, 5);
                    int theatreId = rs.getInt("theatre_id");
                    String theatreName = rs.getString("name");
                    int theatrePrice = rs.getInt("price");

                    showtimeCombo.getItems().add(timeText);
                    timeToShowtimeId.put(timeText, showtimeId);
                    timeToTheatreId.put(timeText, theatreId);
                    timeToTheatreName.put(timeText, theatreName);
                    timeToTheatrePrice.put(timeText, theatrePrice);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleShowtimeSelection(String selectedTime) {
        if (selectedTime == null) return;
        Integer stId = timeToShowtimeId.get(selectedTime);
        Integer thId = timeToTheatreId.get(selectedTime);
        String theatreName = timeToTheatreName.get(selectedTime);
        Integer theatrePrice = timeToTheatrePrice.get(selectedTime);

        if (stId == null || thId == null || theatrePrice == null) return;

        selectedShowtimeId = stId;
        selectedTheatreId = thId;
        currentSeatPrice = theatrePrice;
        theatreLabel.setText("Theatre: " + (theatreName != null ? theatreName : "") + " | Price: " + currentSeatPrice);

        generateSeatGrid(selectedShowtimeId);
    }

    private void generateSeatGrid(int showtimeId) {
        seatGrid.getChildren().clear();
        resetBookingSummary();

        final String sql = """
            SELECT si.`showtimes-seat-id`, s.row, s.seat_number, si.status
            FROM showtimesid si
            JOIN seats s ON s.seatsid = si.seat_id
            WHERE si.showtime_id = ?
            ORDER BY s.row, s.seat_number
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, showtimeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int showtimeSeatId = rs.getInt("showtimes-seat-id");
                    int row = rs.getInt("row");
                    int col = rs.getInt("seat_number");
                    int status = rs.getInt("status");

                    Button seatBtn = new Button(row + "-" + col);
                    seatBtn.setPrefSize(40, 40);

                    if (status == 0) {
                        seatBtn.getStyleClass().add("seat-booked");
                        seatBtn.setDisable(true);
                    } else {
                        seatBtn.getStyleClass().add("seat-free");
                        seatBtn.setOnAction(e -> handleSeatSelection(showtimeSeatId, row, col, seatBtn));
                    }

                    seatGrid.add(seatBtn, col - 1, row - 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
String seatlist ="";
int tp =0;
    private void handleSeatSelection(int showtimeSeatId, int row, int col, Button seatBtn) {
        final String update = "UPDATE showtimesid SET status=0 WHERE `showtimes-seat-id`=? AND status=1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(update)) {
            ps.setInt(1, showtimeSeatId);
            int updated = ps.executeUpdate();
            if (updated > 0) {

                seatBtn.getStyleClass().remove("seat-free");
                seatBtn.getStyleClass().add("seat-selected");
                seatBtn.setDisable(true);


                selectedShowtimeSeatIds.add(showtimeSeatId);
                addSeatToBooking(row + "-" + col, currentSeatPrice);
                seatlist+=row+"-" + col +", ";
            } else {
                showAlert("Someone else booked this seat already. Please try again!");
                generateSeatGrid(selectedShowtimeId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addSeatToBooking(String seatLabel, int price) {
        bookingTable.getItems().add(new BookingRow(seatLabel, price));
        totalPrice += price;
        totalPriceLabel.setText("Total: " + totalPrice);
    }

    private void confirmPurchase(ActionEvent event) {
        String username = usernameField.getText();
        if (username == null || username.isBlank()) {
            showAlert("Please enter username.");
            return;
        }
        if (selectedShowtimeId < 0) {
            showAlert("Select movie, date, and showtime first.");
            return;
        }
        if (selectedShowtimeSeatIds.isEmpty()) {
            showAlert("Select at least one seat.");
            return;
        }


        final String insertTicketSql = """
        INSERT INTO tickets (username, showtimesid, showtimes_seatid, price, PNR, status)
        VALUES (?, ?, ?, ?, ?, 0)
        """;
        int pnrnum=0;
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(insertTicketSql)) {
                for (int stSeatId : selectedShowtimeSeatIds) {
                    Integer pnr = generatePNR();
                    pnrnum = pnr;
                    ps.setString(1, username);
                    ps.setInt(2, selectedShowtimeId);
                    ps.setInt(3, stSeatId);
                    ps.setInt(4, currentSeatPrice);
                    ps.setInt(5, pnr);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            showAlert("Purchase confirmed for " + username + ". Total: " + totalPrice);
            tp = totalPrice;
            resetBookingSummary();
            generateSeatGrid(selectedShowtimeId);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Purchase failed. Please try again.");
        }

        try {
            FXMLLoader ld = new FXMLLoader(getClass().getResource("showreceipt.fxml"));
            Parent root = ld.load();
            ReceiptController controller = ld.getController();
            controller.setReceiptData(username, movieCombo.getValue(),
                    datePicker.getValue().toString(),
                    showtimeCombo.getValue(),
                    seatlist,
                    tp,
                    pnrnum,
                    java.time.LocalDateTime.now());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 600));

        }catch (Exception e) {e.printStackTrace();}

    }



    private int generatePNR() {
        Random rand = new Random();
        return 100_000_000 + rand.nextInt(900_000_000);
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }

    private void go(ActionEvent event, String fxml, Integer width, Integer height, String message) {
        try {
            Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (javafx.stage.Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root, width,height));
            stage.setTitle(message);
            stage.show();
        }
        catch (Exception e) {e.printStackTrace();}
    }

    @FXML void Return(ActionEvent event){ go(event, "usermainpage.fxml", 760, 915, "Movies Ticketing System"); }
}