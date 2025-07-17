package com.moviesapp.movies.Repositories.Movies;

import com.moviesapp.movies.Models.Movies.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
}
