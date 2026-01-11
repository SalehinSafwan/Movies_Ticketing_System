package com.example.androidapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuyTicketsActivity extends AppCompatActivity {
    private EditText usernameField;
    private Spinner movieSpinner, showtimeSpinner;
    private DatePicker datePicker;
    private GridLayout seatGrid;
    private RecyclerView selectedSeatsRecyclerView;
    private TextView totalPriceText;
    private Button confirmButton;

    private List<Movie> movies = new ArrayList<>();
    private List<String> movieIds = new ArrayList<>();
    private List<String> movieTitles = new ArrayList<>();

    private List<Showtime> showtimes = new ArrayList<>();
    private List<String> showtimeIds = new ArrayList<>();
    private List<String> showtimeLabels = new ArrayList<>();

    private List<Seat> selectedSeats = new ArrayList<>();
    private SelectedSeatsAdapter selectedSeatsAdapter;

    private DatabaseReference moviesRef, showtimesRef, seatsRef, purchasesRef;

    private String selectedMovieId = null;
    private String selectedShowtimeId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_tickets);

        usernameField = findViewById(R.id.usernameField);
        movieSpinner = findViewById(R.id.movieSpinner);
        showtimeSpinner = findViewById(R.id.showtimeSpinner);
        datePicker = findViewById(R.id.datePicker);
        seatGrid = findViewById(R.id.seatGrid);
        selectedSeatsRecyclerView = findViewById(R.id.selectedSeatsRecyclerView);
        totalPriceText = findViewById(R.id.totalPriceText);
        confirmButton = findViewById(R.id.confirmButton);

        selectedSeatsAdapter = new SelectedSeatsAdapter(selectedSeats);
        selectedSeatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectedSeatsRecyclerView.setAdapter(selectedSeatsAdapter);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        moviesRef = db.getReference("movies");
        showtimesRef = db.getReference("showtimes");
        seatsRef = db.getReference("showtimeSeats");
        purchasesRef = db.getReference("purchases");

        loadMovies();

        // When movie changes, reload showtimes for the chosen date
        movieSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < movieIds.size()) {
                    selectedMovieId = movieIds.get(position);
                    String date = getSelectedDateString();
                    loadShowtimesFor(selectedMovieId, date);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // When date changes, reload showtimes (if movie selected)
        datePicker.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> {
            if (selectedMovieId != null) {
                String date = getSelectedDateString();
                loadShowtimesFor(selectedMovieId, date);
            }
        });

        // When showtime changes, load seats grid
        showtimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < showtimeIds.size()) {
                    selectedShowtimeId = showtimeIds.get(position);
                    loadSeatsForShowtime(selectedShowtimeId);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        confirmButton.setOnClickListener(v -> confirmPurchase());
    }

    // Load movies into spinner (titles list displayed, keep ids for mapping)
    private void loadMovies() {
        moviesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                movies.clear();
                movieIds.clear();
                movieTitles.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Movie m = child.getValue(Movie.class);
                    if (m != null) {
                        movies.add(m);
                        movieIds.add(child.getKey());
                        movieTitles.add(m.title);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(BuyTicketsActivity.this,
                        android.R.layout.simple_spinner_item, movieTitles);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                movieSpinner.setAdapter(adapter);

                // Select first movie by default if available
                if (!movieIds.isEmpty()) {
                    selectedMovieId = movieIds.get(0);
                    String date = getSelectedDateString();
                    loadShowtimesFor(selectedMovieId, date);
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Convert DatePicker selection to YYYY-MM-DD
    private String getSelectedDateString() {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1; // 0-based
        int year = datePicker.getYear();
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    // Load showtimes for movieId and date, filter status=1
    private void loadShowtimesFor(String movieId, String date) {
        showtimesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                showtimes.clear();
                showtimeIds.clear();
                showtimeLabels.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Showtime s = child.getValue(Showtime.class);
                    if (s != null
                            && s.status == 1
                            && s.movieId != null
                            && s.movieId.equals(movieId)
                            && s.date != null
                            && s.date.equals(date)) {

                        showtimes.add(s);
                        showtimeIds.add(child.getKey());
                        String label = s.time + " • " + s.theatreName;
                        showtimeLabels.add(label);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(BuyTicketsActivity.this,
                        android.R.layout.simple_spinner_item, showtimeLabels);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                showtimeSpinner.setAdapter(adapter);

                // Clear seats and price if no showtimes
                selectedSeats.clear();
                selectedSeatsAdapter.notifyDataSetChanged();
                updateTotalPrice();
                seatGrid.removeAllViews();

                // Auto-select first showtime if available
                if (!showtimeIds.isEmpty()) {
                    selectedShowtimeId = showtimeIds.get(0);
                    loadSeatsForShowtime(selectedShowtimeId);
                } else {
                    selectedShowtimeId = null;
                    Toast.makeText(BuyTicketsActivity.this, "No showtimes for selected date.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Build seat grid from Firebase: showtimeSeats/{showtimeId}
    private void loadSeatsForShowtime(String showtimeId) {
        if (showtimeId == null) return;

        seatsRef.child(showtimeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                seatGrid.removeAllViews();
                selectedSeats.clear();
                selectedSeatsAdapter.notifyDataSetChanged();
                updateTotalPrice();

                if (!snapshot.exists()) {
                    Toast.makeText(BuyTicketsActivity.this, "No seats found for this showtime", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 1) Group seats by numeric row
                Map<Integer, List<Seat>> byRow = new HashMap<>();
                int maxColumns = 0;

                for (DataSnapshot child : snapshot.getChildren()) {
                    Seat seat = child.getValue(Seat.class);
                    if (seat == null) continue;

                    seat.seatId = child.getKey();

                    int rowNum = seat.row; // row stored as string, convert to int
                    byRow.computeIfAbsent(rowNum, k -> new ArrayList<>()).add(seat);

                    maxColumns = Math.max(maxColumns, seat.seatNumber);
                }

                // 2) Sort rows numerically
                List<Integer> rows = new ArrayList<>(byRow.keySet());
                rows.sort(Integer::compare);

                seatGrid.setColumnCount(maxColumns);

                // 3) Place buttons by row/column
                for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                    int rowNum = rows.get(rowIndex);
                    List<Seat> list = byRow.get(rowNum);

                    // sort seats in this row by seatNumber
                    list.sort((a, b) -> Integer.compare(a.seatNumber, b.seatNumber));

                    for (int colIndex = 0; colIndex < list.size(); colIndex++) {
                        Seat seat = list.get(colIndex);

                        Button btn = new Button(BuyTicketsActivity.this);
                        btn.setText("R" + rowNum + "S" + seat.seatNumber);
                        btn.setTextColor(Color.BLACK);
                        btn.setPadding(8, 8, 8, 8);

                        if (seat.status == 1) {
                            btn.setBackgroundColor(Color.parseColor("#00FF00")); // free
                            btn.setEnabled(true);
                        } else {
                            btn.setBackgroundColor(Color.parseColor("#FF0000")); // booked
                            btn.setEnabled(false);
                        }

                        btn.setOnClickListener(v -> {
                            btn.setBackgroundColor(Color.parseColor("#FFFF00")); // selected
                            btn.setEnabled(false);
                            selectedSeats.add(seat);
                            selectedSeatsAdapter.notifyDataSetChanged();
                            updateTotalPrice();

                            // Update Firebase status
                            seatsRef.child("status").setValue(1);
                        });

                        GridLayout.Spec rowSpec = GridLayout.spec(rowIndex);
                        GridLayout.Spec colSpec = GridLayout.spec(colIndex);
                        GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, colSpec);
                        params.setMargins(6, 6, 6, 6);
                        seatGrid.addView(btn, params);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateTotalPrice() {
        int total = 0;
        for (Seat s : selectedSeats) {
            total += s.price;
        }
        totalPriceText.setText("Total Price: " + total);
    }

    private int calculateTotal() {
        int total = 0;
        for (Seat s : selectedSeats) total += s.price;
        return total;
    }

    private void confirmPurchase() {
        String username = usernameField.getText().toString().trim();
        if (username.isEmpty()) {
            Toast.makeText(this, "Enter username", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedMovieId == null || selectedShowtimeId == null) {
            Toast.makeText(this, "Select movie, date, and showtime", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedSeats.isEmpty()) {
            Toast.makeText(this, "No seats selected", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> seatLabels = new ArrayList<>();
        ArrayList<String> pnrList = new ArrayList<>();
        int totalPrice = 0;


        for (Seat s : selectedSeats) {
            int pnr = (int)(Math.random() * 90000000) + 10000000;
            Map<String, Object> purchase = new HashMap<>();
            purchase.put("username", username);
            purchase.put("showtimes_seatid", s.seatId);
            purchase.put("showtimeId", selectedShowtimeId);
            purchase.put("status", 0);
            purchase.put("PNR", pnr);
            purchase.put("price", s.price);

            seatLabels.add("Row " + s.row + " Seat " + s.seatNumber);
            pnrList.add(String.valueOf(pnr));
            totalPrice += s.price;


            purchasesRef.push().setValue(purchase);


            seatsRef.child(selectedShowtimeId)
                    .child(s.seatId)
                    .child("status")
                    .setValue(0);
        }

        Toast.makeText(this, "Purchase confirmed!", Toast.LENGTH_SHORT).show();

        // Reset UI
        selectedSeats.clear();
        selectedSeatsAdapter.notifyDataSetChanged();
        updateTotalPrice();
        loadSeatsForShowtime(selectedShowtimeId);

        Intent intent = new Intent(BuyTicketsActivity.this, ReceiptActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("showtimeId", selectedShowtimeId);
        intent.putStringArrayListExtra("pnrs", pnrList);
        intent.putStringArrayListExtra("seats", seatLabels);
        intent.putExtra("totalPrice", totalPrice);
        startActivity(intent);

    }
}