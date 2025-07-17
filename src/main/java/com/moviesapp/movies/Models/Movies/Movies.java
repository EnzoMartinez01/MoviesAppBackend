package com.moviesapp.movies.Models.Movies;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.moviesapp.movies.Deserializer.Movies.GenreDeserializer;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_movies")
@Data
public class Movies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, length = 1000)
    private String synopsis;
    @Column(nullable = false)
    private LocalDate releaseDate;
    @Column(nullable = false)
    private Integer durationInMinutes;
    @Column(nullable = false)
    private String ranking;
    @Column(nullable = false)
    private String language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @JsonDeserialize(using = GenreDeserializer.class)
    @JsonBackReference
    private Genre genre;

    private String urlMovie;

    private String urlTrailer;
    private String coverImage;
    private LocalDate premiereDate;
    private Boolean isActive;
}
