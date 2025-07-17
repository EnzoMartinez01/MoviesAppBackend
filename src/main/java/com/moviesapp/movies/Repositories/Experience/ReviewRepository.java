package com.moviesapp.movies.Repositories.Experience;

import com.moviesapp.movies.Models.Authentication.Users;
import com.moviesapp.movies.Models.Experience.Review;
import com.moviesapp.movies.Models.Movies.Movies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Page<Review> findByMovie(Movies movie, Pageable pageable);
    Page<Review> findByUser(Users user, Pageable pageable);
}
