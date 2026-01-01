package com.example.desktop;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class AddCancelShowController {
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

    public class Movie {
        private final int movieId;
        private final String title;
        public Movie(int movieId, String title) { this.movieId = movieId; this.title = title; }
        public int getMovieId() { return movieId; }
        public String getTitle() { return title; }
        @Override public String toString() { return title; }
    }

    public class Theatre {
        private final int theatreId;
        private final String name;
        public Theatre(int theatreId, String name) { this.theatreId = theatreId; this.name = name; }
        public int getTheatreId() { return theatreId; }
        public String getName() { return name; }
        @Override public String toString() { return name; }
    }

    public class ShowtimeItem {
        private final int showtimeId;
        private final String label; // e.g., "Movie @ Theatre on 2026-01-03 18:30"
        public ShowtimeItem(int showtimeId, String label) { this.showtimeId = showtimeId; this.label = label; }
        public int getShowtimeId() { return showtimeId; }
        @Override public String toString() { return label; }
    }

    @FXML private ComboBox<Movie> movieComboBox;
    @FXML private ComboBox<Theatre> theatreComboBox;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<ShowtimeItem> cancelshowcombo;
    @FXML private TextField newTimeField;

    @FXML
    public void initialize() {
        loadMovies();
        loadTheatres();
        datePicker.valueProperty().addListener((obs, oldV, newV) -> loadTimesForSelection());
        theatreComboBox.valueProperty().addListener((obs, oldV, newV) -> loadTimesForSelection());

        loadCancelableShowtimes();
    }

    private void loadCancelableShowtimes() {
        String sql = """
        SELECT st.showtime_id, m.title, th.name, st.date, st.time
        FROM Showtimes st
        JOIN Movies m ON st.movie_id = m.movie_id
        JOIN theatreid th ON st.theatre_id = th.theatre_id
        WHERE st.status = 1
        ORDER BY st.date, st.time
        """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            var items = FXCollections.<ShowtimeItem>observableArrayList();
            while (rs.next()) {
                String label = String.format("%s @ %s on %s %s",
                        rs.getString("title"),
                        rs.getString("name"),
                        rs.getDate("date"),
                        rs.getTime("time").toLocalTime().toString().substring(0,5));
                items.add(new ShowtimeItem(rs.getInt("showtime_id"), label));
            }
            cancelshowcombo.setItems(items);
        } catch (Exception e) { e.printStackTrace(); }

    }

    private void loadTimesForSelection() {
        LocalDate d = datePicker.getValue();
        Theatre th = theatreComboBox.getValue();
        if(d==null) {timeComboBox.setItems(FXCollections.observableArrayList()); return;}

        String base = "SELECT DISTINCT time FROM Showtimes WHERE date = ?";
        boolean filterByTheatre = (th != null);

        String sql = filterByTheatre ? base + " AND theatre_id = ? ORDER BY time"
                : base + " ORDER BY time";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(d));
            if (filterByTheatre) ps.setInt(2, th.getTheatreId());

            try (ResultSet rs = ps.executeQuery()) {
                var times = FXCollections.<String>observableArrayList();
                while (rs.next()) {
                    String hhmm = rs.getTime("time").toLocalTime().toString().substring(0,5); // "HH:mm"
                    times.add(hhmm);
                }
                timeComboBox.setItems(times);
            }
        } catch (Exception e) { e.printStackTrace(); }

    }

    private void loadMovies() {
        String sql = "SELECT movie_id, title FROM movies ORDER BY title";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            var items = FXCollections.<Movie>observableArrayList();
            while (rs.next()) items.add(new Movie(rs.getInt("movie_id"), rs.getString("title")));
            movieComboBox.setItems(items);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadTheatres() {
        String sql = "SELECT theatre_id, name FROM theatreid ORDER BY name";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            var items = FXCollections.<Theatre>observableArrayList();
            while (rs.next()) items.add(new Theatre(rs.getInt("theatre_id"), rs.getString("name")));
            theatreComboBox.setItems(items);
        } catch (Exception e) { e.printStackTrace(); }
    }



    @FXML private void AddShow() {
        Movie m = movieComboBox.getValue();
        Theatre th = theatreComboBox.getValue();
        LocalDate d = datePicker.getValue();

        String typedTime = newTimeField.getText() != null ? newTimeField.getText().trim() : "";
        String selectedTime = timeComboBox.getValue();

        String timeText = (!typedTime.isEmpty()) ? typedTime : selectedTime;

        if (m == null || th == null || d == null || timeText == null || timeText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please select movie, theatre, date, and time (or type a new time).");
            return;
        }
        if (!timeText.matches("\\d{2}:\\d{2}")) {
            showAlert(Alert.AlertType.ERROR, "Invalid time format", "Use, HH:MM.");
            return;
        }

        String insert = "INSERT INTO showtimes (movie_id, theatre_id, date, time, status,revenue) " +
                "VALUES (?, ?, ?, ?, 1,0)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, m.getMovieId());
            ps.setInt(2, th.getTheatreId());
            ps.setDate(3, java.sql.Date.valueOf(d));
            ps.setTime(4, java.sql.Time.valueOf(timeText + ":00"));

            ps.executeUpdate();

            int showtimeId;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) showtimeId = keys.getInt(1);
                else { showAlert(Alert.AlertType.WARNING, "Insertion Error", "No showtime ID returned."); return; }
            }

            // (seats fixed layout : 10x10)
            generateSeats(c, showtimeId,th.getTheatreId(), 10, 10);

            showAlert(Alert.AlertType.CONFIRMATION, "Success", "Show added and seats generated.");


            loadTimesForSelection();
            loadCancelableShowtimes();

        } catch (SQLIntegrityConstraintViolationException dup) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Show", "A show already exists for that hall, date, and time.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateSeats(Connection c, int showtimeId,int TheatreID, int rows, int cols) throws Exception {
        String sql = "INSERT INTO showtimesid (showtime_id, seat_id, status) SELECT ?, s.seatsid, 1 " +
                "FROM seats s WHERE s.theatreid = ?";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, showtimeId);
            ps.setInt(2, TheatreID);
            ps.executeUpdate();
        }

    }

    @FXML
    private void CancelShow() {
        ShowtimeItem item = cancelshowcombo.getValue();
        if (item == null) {
            showAlert(Alert.AlertType.WARNING, "Blank Selection", "Please select a show to cancel.");
            return;
        }

        String sql = "UPDATE showtimes SET status=0 WHERE showtime_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, item.getShowtimeId());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.CONFIRMATION, "Success", "Show has been cancelled.");
                loadCancelableShowtimes();
                // Compute cancellation loss and update Revenue table here
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to cancel the show.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




        private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML void Back(ActionEvent event) {go(event, "admindashboard.fxml", 1300, 900, "Admin Dashboard");}
}
