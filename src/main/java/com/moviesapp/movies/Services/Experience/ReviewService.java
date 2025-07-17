package com.moviesapp.movies.Services.Experience;

import com.moviesapp.movies.Dto.Experience.ReviewDto;
import com.moviesapp.movies.Models.Authentication.Users;
import com.moviesapp.movies.Models.Experience.Review;
import com.moviesapp.movies.Models.Movies.Movies;
import com.moviesapp.movies.Repositories.Authentication.UsersRepository;
import com.moviesapp.movies.Repositories.Experience.ReviewRepository;
import com.moviesapp.movies.Repositories.Movies.MoviesRepository;
import com.moviesapp.movies.Services.Common.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ReviewService {
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final CommonService commonService;
    private final MoviesRepository moviesRepository;

    public ReviewService (ReviewRepository reviewRepository,
                          CommonService commonService,
                          MoviesRepository moviesRepository) {
        this.reviewRepository = reviewRepository;
        this.commonService = commonService;
        this.moviesRepository = moviesRepository;
    }

    // Get All Reviews
    public Page<ReviewDto> getAllReviews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewRepository.findAll(pageable);
        return reviews.map(this::mapToDto);
    }

    // Get Reviews by User
    public Page<ReviewDto> getReviewsByUser(int page, int size) {
        Users user = commonService.authenticationCurrent();
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewRepository.findByUser(user, pageable);
        return reviews.map(this::mapToDto);
    }

    // Get Reviews by Movie
    public Page<ReviewDto> getReviewsByMovie(int page, int size, Integer idMovie) {
        Movies movie = moviesRepository.findById(idMovie)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewRepository.findByMovie(movie, pageable);
        return reviews.map(this::mapToDto);
    }

    // Map To Dto
    public ReviewDto mapToDto (Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setIdReview(review.getId());
        dto.setFullname(review.getUser().getFullname());
        dto.setUsername(review.getUser().getUsername());
        dto.setTitleName(review.getMovie().getTitle());
        dto.setComment(review.getComment());
        dto.setRating(review.getRating());
        dto.setCreateDate(review.getCreated_at());
        return dto;
    }

    // Create a new Review
    @Transactional
    public Review addReview(Integer idMovie, Review review) {
        try {
            Movies movie = moviesRepository.findById(idMovie)
                    .orElseThrow(() -> new RuntimeException("Movie not found"));
            Users user = commonService.authenticationCurrent();

            review.setUser(user);
            review.setMovie(movie);
            review.setCreated_at(LocalDateTime.now());
            logger.info("Review {} created successfully", review.getMovie().getTitle());
            return reviewRepository.save(review);
        } catch (Exception e){
            logger.error("Error creating Review", e);
            throw new RuntimeException("Error creating Review", e);
        }
    }

    // Update a Review
    @Transactional
    public Review updateReview(Integer idReview, Review review) {
        try {
            Review existingReview = reviewRepository.findById(idReview)
                    .orElseThrow(() -> new RuntimeException("Review not found"));
            if (review.getComment() != null && !review.getComment().isEmpty()) {
                existingReview.setComment(review.getComment());
            }
            if  (review.getRating() != null) {
                existingReview.setRating(review.getRating());
            }
            logger.info("Review {} updated successfully", existingReview.getMovie().getTitle());
            return reviewRepository.save(existingReview);
        } catch (Exception e) {
            logger.error("Error updating Review", e);
            throw new RuntimeException("Error updating Review", e);
        }
    }

    //Eliminate Review
    public void eliminateReview (Integer idReview) {
        try {
            Review review = reviewRepository.findById(idReview)
                    .orElseThrow(() -> new RuntimeException("Review not found"));

            reviewRepository.delete(review);
            logger.info("Review deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleted review", e);
            throw new RuntimeException("Error deleted review", e);
        }
    }
}
