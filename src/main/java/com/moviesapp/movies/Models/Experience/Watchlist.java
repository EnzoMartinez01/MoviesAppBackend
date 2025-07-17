package com.moviesapp.movies.Models.Experience;

import com.moviesapp.movies.Models.Authentication.Users;
import com.moviesapp.movies.Models.Movies.Movies;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "t_watchlist")
@Data
public class Watchlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Users user;

    @Column(nullable = false)
    private String listName;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "watchlist_movies",
            joinColumns = @JoinColumn(name = "watchlist_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    private List<Movies> movies;
}
