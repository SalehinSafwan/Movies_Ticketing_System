package com.example.androidapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CancelTicketActivity extends AppCompatActivity {

    private EditText cancelUsername, cancelPNR;
    private Button cancelButton;
    private DatabaseReference ticketsRef, seatsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cancel_ticket);

        cancelUsername = findViewById(R.id.cancelUsername);
        cancelPNR = findViewById(R.id.cancelPNR);
        cancelButton = findViewById(R.id.cancelButton);

        ticketsRef = FirebaseDatabase.getInstance().getReference("tickets");
        seatsRef = FirebaseDatabase.getInstance().getReference("showtimeSeats");

        cancelButton.setOnClickListener(v -> cancelTicket());
    }

    private void cancelTicket() {
        String username = cancelUsername.getText().toString().trim();
        String pnr = cancelPNR.getText().toString().trim();

        if (username.isEmpty() || pnr.isEmpty()) {
            Toast.makeText(this, "Enter both Username and PNR", Toast.LENGTH_SHORT).show();
            return;
        }

        ticketsRef.orderByChild("PNR").equalTo(Long.parseLong(pnr))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Toast.makeText(CancelTicketActivity.this, "No ticket found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (DataSnapshot child : snapshot.getChildren()) {
                            Ticket ticket = child.getValue(Ticket.class);
                            if (ticket != null && ticket.username.equals(username)) {

                                child.getRef().child("status").setValue(1);


                                seatsRef.child(ticket.showtime_id)
                                        .child(ticket.showtimes_seat_id)
                                        .child("status")
                                        .setValue(0);

                                Toast.makeText(CancelTicketActivity.this, "Ticket cancelled successfully!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}
