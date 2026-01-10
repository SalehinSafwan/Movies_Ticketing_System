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

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Movie> movieList;

    public MovieAdapter(List<Movie> movieList) {
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movies, parent, false);
        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.title.setText(movie.title);
        holder.releaseDate.setText("Release: "+ (movie.release_date!=null ? movie.release_date : ""));
        holder.genre.setText("Genre: "+ (movie.genre!=null ? movie.genre : ""));
        holder.duration.setText("Duration: " + movie.duration);

        Glide.with(holder.poster.getContext())
                .load(movie.posterUrl)
                .into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView title, releaseDate,genre, duration;
        ImageView poster;

        MovieViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.movieTitle);
            releaseDate = itemView.findViewById(R.id.movieReleaseDate);
            genre = itemView.findViewById(R.id.movieGenre);
            duration = itemView.findViewById(R.id.movieDuration);
            poster = itemView.findViewById(R.id.posterImage);
        }
    }
}