package com.moviesapp.movies.Dto.Experience;

import com.moviesapp.movies.Dto.Movies.MoviesDto;
import lombok.Data;

import java.util.List;

@Data
public class WatchListDto {
    private Integer idWatchlist;
    private String listName;

    private String fullname;
    private String username;

    List<MoviesDto> movies;
}
