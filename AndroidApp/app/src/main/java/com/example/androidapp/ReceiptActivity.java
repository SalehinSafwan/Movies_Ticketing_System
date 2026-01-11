package com.example.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ReceiptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt);

        TextView receiptTitle = findViewById(R.id.receiptTitle);
        TextView receiptUsername = findViewById(R.id.receiptUsername);
        TextView receiptShowtime = findViewById(R.id.receiptShowtime);
        TextView receiptPNRs = findViewById(R.id.receiptPNRs);
        TextView receiptSeats = findViewById(R.id.receiptSeats);
        TextView receiptTotal = findViewById(R.id.receiptTotal);

        // Get data from Intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String showtimeId = intent.getStringExtra("showtimeId");
        ArrayList<String> pnrList = intent.getStringArrayListExtra("pnrs");
        ArrayList<String> seatLabels = intent.getStringArrayListExtra("seats");
        int totalPrice = intent.getIntExtra("totalPrice", 0);

        // Fill views
        receiptUsername.setText("Username: " + username);
        receiptShowtime.setText("Showtime ID: " + showtimeId);
        receiptPNRs.setText("PNRs: " + (pnrList != null ? TextUtils.join(", ", pnrList) : ""));
        receiptSeats.setText("Seats: " + (seatLabels != null ? TextUtils.join(", ", seatLabels) : ""));
        receiptTotal.setText("Total Price: " + totalPrice);
    }
}