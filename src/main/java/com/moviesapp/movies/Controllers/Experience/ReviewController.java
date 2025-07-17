package com.moviesapp.movies.Controllers.Experience;

import com.moviesapp.movies.Dto.Experience.ReviewDto;
import com.moviesapp.movies.Models.Experience.Review;
import com.moviesapp.movies.Services.Experience.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/review")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController (ReviewService reviewService){
        this.reviewService = reviewService;
    }

    // Get all Reviews
    @GetMapping("/getAll")
    public Page<ReviewDto> getAllReviews(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        return reviewService.getAllReviews(page, size);
    }

    // Get Review by User
    @GetMapping("/myReviews")
    public Page<ReviewDto> getReviewsByUser(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return reviewService.getReviewsByUser(page, size);
    }

    // Get Reviews by Movie
    @GetMapping("/{idMovie}/reviews")
    public Page<ReviewDto> getReviewsByMovie(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam Integer idMovie) {
        return reviewService.getReviewsByMovie(page, size, idMovie);
    }

    // Add new Review
    @PostMapping("/{idMovie}/addReview")
    public ResponseEntity<Map<String, String>> addRevie(@PathVariable Integer idMovie,
                                                        @RequestBody Review review) {
        try {
            Review savedReview = reviewService.addReview(idMovie, review);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Review created successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            throw new RuntimeException("Error creating review", e);
        }
    }

    // Updated Review
    @PutMapping("/updateReview/{idReview}")
    public ResponseEntity<Map<String, String>> updateReview(@PathVariable Integer idReview,
                                                            @RequestBody Review updateReview) {
        try {
            Review review = reviewService.updateReview(idReview, updateReview);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Review updated successfully");
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Eliminated Review
    @DeleteMapping("/deleteReview/{idReview}")
    public ResponseEntity<Map<String, String>> deleteReview(@PathVariable Integer idReview) {
        try {
            reviewService.eliminateReview(idReview);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Review deleted successfully");
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
