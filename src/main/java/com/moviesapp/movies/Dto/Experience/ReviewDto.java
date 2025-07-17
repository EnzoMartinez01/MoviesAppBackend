package com.moviesapp.movies.Dto.Experience;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDto {
    private Integer idReview;
    private String fullname;
    private String username;

    private String titleName;
    private String comment;
    private Double rating;
    private LocalDateTime createDate;
}