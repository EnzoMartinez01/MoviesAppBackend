package com.moviesapp.movies.Models.Streaming;

import com.moviesapp.movies.Models.Authentication.Users;
import com.moviesapp.movies.Models.Movies.Movies;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_watch_history")
@Data
public class WatchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Movies movie;

    private LocalDateTime timestamp;

    private LocalDateTime playbackDate;
}
