package com.moviesapp.movies.Dto.Movies;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MoviesDto {
    private Integer idMovie;
    private String titleMovie;
    private String synopsisMovie;
    private String genreName;
    private LocalDate releaseDate;
    private Integer duration;
    private String pg;
    private String language;
    private String movieUrl;
    private String urlTrailer;
    private String movieImage;
}
