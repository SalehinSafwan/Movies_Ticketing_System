package com.example.androidapp;

import android.os.Bundle;
import android.util.Log;
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

public class VerifyTicketActivity extends AppCompatActivity {

    private EditText verifyUsername, verifyPNR;
    private Button verifyButton;
    private DatabaseReference ticketsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_ticket);

        verifyUsername = findViewById(R.id.verifyUsername);
        verifyPNR = findViewById(R.id.verifyPNR);
        verifyButton = findViewById(R.id.verifyButton);

        ticketsRef = FirebaseDatabase.getInstance().getReference("tickets");

        verifyButton.setOnClickListener(v -> verifyTicket());
    }

    private void verifyTicket() {
        String username = verifyUsername.getText().toString().trim();
        String pnr = verifyPNR.getText().toString().trim();

        if (username.isEmpty() || pnr.isEmpty()) {
            Toast.makeText(this, "Enter both Username and PNR", Toast.LENGTH_SHORT).show();
            return;
        }
        long pnrValue;
        try {
            pnrValue = Long.parseLong(pnr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "PNR must be a number", Toast.LENGTH_SHORT).show();
            return;
        }


        ticketsRef.orderByChild("PNR").equalTo(Long.parseLong(pnr))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Toast.makeText(VerifyTicketActivity.this, "❌ Ticket not found", Toast.LENGTH_LONG).show();
                            return;
                        }

                        boolean matched = false;
                        for (DataSnapshot child : snapshot.getChildren()) {
                            String dbUsername = child.child("username").getValue(String.class);
                            Integer status = child.child("Status").getValue(Integer.class);

                            Log.d("VerifyTicket", "Found ticket: username=" + dbUsername + " Status=" + status);


                            if (dbUsername != null && dbUsername.equals(username)) {
                                matched = true;
                                if (status != null) {
                                    switch (status) {
                                        case 0:
                                            Toast.makeText(VerifyTicketActivity.this, "✅ Ticket is VALID", Toast.LENGTH_LONG).show();
                                            break;
                                        case 1:
                                            Toast.makeText(VerifyTicketActivity.this, "⚠️ Ticket has been CANCELLED", Toast.LENGTH_LONG).show();
                                            break;
                                        case 2:
                                            Toast.makeText(VerifyTicketActivity.this, "🚫 Show CANCELLED by Authority. Refund in 3-5days.", Toast.LENGTH_LONG).show();
                                            break;
                                        default:
                                            Toast.makeText(VerifyTicketActivity.this, "Unknown ticket status", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }

                        if (!matched) {
                            Toast.makeText(VerifyTicketActivity.this, "❌ Ticket not found for this username", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(VerifyTicketActivity.this, "Error verifying ticket", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
