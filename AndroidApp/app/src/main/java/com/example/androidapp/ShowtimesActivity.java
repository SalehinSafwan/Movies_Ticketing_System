package com.example.androidapp;

import android.os.Bundle;

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
import java.util.List;

public class ShowtimesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ShowtimeAdapter adapter;
    private List<Showtime> showtimeList = new ArrayList<>();
    private DatabaseReference showtimesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showtimes);

        recyclerView = findViewById(R.id.showtimesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ShowtimeAdapter(showtimeList);
        recyclerView.setAdapter(adapter);

        showtimesRef = FirebaseDatabase.getInstance().getReference("showtimes");

        showtimesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showtimeList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Showtime s = child.getValue(Showtime.class);
                    if (s != null) showtimeList.add(s);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
