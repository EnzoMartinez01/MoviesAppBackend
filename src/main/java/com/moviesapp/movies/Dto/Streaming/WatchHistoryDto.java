package com.moviesapp.movies.Dto.Streaming;

import com.moviesapp.movies.Dto.Movies.MoviesDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WatchHistoryDto {
    private Integer idWatchHistory;
    private LocalDateTime timestamp;
    private LocalDateTime playbackDate;

    private String fullname;
    private String username;

    private MoviesDto movie;
}
