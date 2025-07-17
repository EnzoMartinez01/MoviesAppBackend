package com.moviesapp.movies.Dto.Movies;

import lombok.Data;

import java.util.List;

@Data
public class GenreDto {
    private Integer idGenre;
    private String genreName;
    private List<MoviesDto> movies;
}
