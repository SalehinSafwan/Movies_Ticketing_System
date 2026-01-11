package com.example.androidapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SelectedSeatsAdapter extends RecyclerView.Adapter<SelectedSeatsAdapter.SeatViewHolder> {
    private List<Seat> selectedSeats;

    public SelectedSeatsAdapter(List<Seat> selectedSeats) {
        this.selectedSeats = selectedSeats;
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_seat, parent, false);
        return new SeatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        Seat s = selectedSeats.get(position);
        holder.seatLabel.setText(String.valueOf(s.row)+ "-" + String.valueOf(s.seatNumber));
        holder.seatPrice.setText(String.valueOf(s.price));
    }

    @Override
    public int getItemCount() {
        return selectedSeats.size();
    }

    static class SeatViewHolder extends RecyclerView.ViewHolder {
        TextView seatLabel, seatPrice;
        SeatViewHolder(View itemView) {
            super(itemView);
            seatLabel = itemView.findViewById(R.id.seatLabel);
            seatPrice = itemView.findViewById(R.id.seatPrice);
        }
    }
}
