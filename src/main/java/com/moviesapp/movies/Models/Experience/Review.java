package com.moviesapp.movies.Models.Experience;

import com.moviesapp.movies.Models.Authentication.Users;
import com.moviesapp.movies.Models.Movies.Movies;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_review")
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Movies movie;

    private String comment;
    @Column(nullable = false)
    private Double rating;
    private LocalDateTime created_at;
}
