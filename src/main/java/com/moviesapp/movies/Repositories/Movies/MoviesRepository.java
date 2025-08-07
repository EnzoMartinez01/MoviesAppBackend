package com.moviesapp.movies.Repositories.Movies;

import com.moviesapp.movies.Models.Movies.Genre;
import com.moviesapp.movies.Models.Movies.Movies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MoviesRepository extends JpaRepository<Movies, Integer> {

    @Query("""
        SELECT m FROM Movies m
        LEFT JOIN m.genre g
        WHERE (:searchTerm IS NULL OR 
               LOWER(m.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
               LOWER(m.synopsis) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
               LOWER(g.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
          AND (:language IS NULL OR LOWER(m.language) = LOWER(:language))
          AND (:ranking IS NULL OR m.ranking = :ranking)
          AND (:isActive IS NULL OR m.isActive = :isActive)
          AND (:releaseDateStart IS NULL OR m.releaseDate >= :releaseDateStart)
          AND (:releaseDateEnd IS NULL OR m.releaseDate <= :releaseDateEnd)
    """)
    Page<Movies> searchMoviesWithFilters(
            @Param("searchTerm") String searchTerm,
            @Param("language") String language,
            @Param("ranking") String ranking,
            @Param("isActive") Boolean isActive,
            @Param("releaseDateStart") LocalDate releaseDateStart,
            @Param("releaseDateEnd") LocalDate releaseDateEnd,
            Pageable pageable
    );

    @Query("""
    SELECT m FROM Movies m
    WHERE (:genre IS NULL OR m.genre = :genre)
      AND (:isActive IS NULL OR m.isActive = :isActive)
    """)
    Page<Movies> findByGenreAndIsActive(
            @Param("genre") Genre genre,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );

    Page<Movies> findByIsActive(Pageable pageable, Boolean isActive);

    @Query(value = """
    SELECT DISTINCT tm.* 
    FROM t_movies tm 
    INNER JOIN t_review tr ON tm.id = tr.movie_id 
    WHERE tr.rating IN (4, 5) 
      AND tm.is_active = :isActive
    """,
            countQuery = """
    SELECT COUNT(DISTINCT tm.id) 
    FROM t_movies tm 
    INNER JOIN t_review tr ON tm.id = tr.movie_id 
    WHERE tr.rating IN (4, 5) 
      AND tm.is_active = :isActive
    """,
            nativeQuery = true)
    Page<Movies> findTopRatedMoviesByActive(@Param("isActive") boolean isActive, Pageable pageable);

}
