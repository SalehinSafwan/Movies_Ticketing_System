package com.example.androidapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import java.util.List;

public class ShowtimeAdapter extends RecyclerView.Adapter<ShowtimeAdapter.ShowtimeViewHolder> {
    private List<Showtime> showtimeList;

    public ShowtimeAdapter(List<Showtime> showtimeList) {
        this.showtimeList = showtimeList;
    }

    @NonNull
    @Override
    public ShowtimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_showtime, parent, false);
        return new ShowtimeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowtimeViewHolder holder, int position) {
        Showtime s = showtimeList.get(position);
        holder.title.setText(s.movieTitle);
        holder.date.setText("Date: " + s.date);
        holder.time.setText("Time: " + s.time);
        holder.theatre.setText("Theatre: " + s.theatreName);

        Glide.with(holder.poster.getContext())
                .load(s.posterUrl)
                .into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return showtimeList.size();
    }

    static class ShowtimeViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, time, theatre;
        ImageView poster;

        ShowtimeViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.showtimeTitle);
            date = itemView.findViewById(R.id.showtimeDate);
            time = itemView.findViewById(R.id.showtimeTime);
            theatre = itemView.findViewById(R.id.showtimeTheatre);
            poster = itemView.findViewById(R.id.showtimePoster);
        }
    }
}
