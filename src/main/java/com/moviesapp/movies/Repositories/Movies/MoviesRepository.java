package com.moviesapp.movies.Repositories.Movies;

import com.moviesapp.movies.Models.Movies.Genre;
import com.moviesapp.movies.Models.Movies.Movies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoviesRepository extends JpaRepository<Movies, Integer> {
    Page<Movies> findByGenreAndIsActive(Pageable pageable, Genre genre, Boolean isActive);

    Page<Movies> findByIsActive(Pageable pageable, Boolean isActive);
}
