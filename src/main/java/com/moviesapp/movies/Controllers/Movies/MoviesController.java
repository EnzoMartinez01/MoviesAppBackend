package com.moviesapp.movies.Controllers.Movies;

import com.moviesapp.movies.Dto.Movies.MoviesDto;
import com.moviesapp.movies.Models.Movies.Movies;
import com.moviesapp.movies.Services.Movies.MoviesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/movies")
public class MoviesController {
    private final MoviesService moviesService;

    public MoviesController(MoviesService moviesService) {
        this.moviesService = moviesService;
    }

    // Get Movies by Filters
    @GetMapping("/search")
    public Page<MoviesDto> searchMovies(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String ranking,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDateEnd,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return moviesService.searchMoviesWithFilters(
                searchTerm, language, ranking, isActive, releaseDateStart, releaseDateEnd, PageRequest.of(page, size)
        );
    }

    // Get all movies
    @GetMapping("/getAll")
    public Page<MoviesDto> getAllMovies(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(defaultValue = "true") Boolean isActive) {
        return moviesService.getAllMovies(page, size, isActive);
    }

    // Get Movie by id
    @GetMapping("/getMovie/{idMovie}")
    public MoviesDto getMovieById(@PathVariable Integer idMovie) {
        return moviesService.getMovieById(idMovie);
    }

    // Get Movies with Genre
    @GetMapping("/getMovies")
    public Page<MoviesDto> getMoviesByGenre(@RequestParam(required = false) Integer idGenre,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(defaultValue = "true") Boolean isActive) {
        return moviesService.getMovieByGenre(page, size, idGenre, isActive);
    }

    // Get Movies by Ranking
    @GetMapping("/top-rated")
    public Page<MoviesDto> getTopRatedMovies(
            @RequestParam(defaultValue = "true") boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return moviesService.getTopRatedMovies(isActive, page, size);
    }

    // Create a new Movie
    @PostMapping("/addMovie")
    public ResponseEntity<Map<String, String>> addMovie(@RequestBody Movies movie) {
        try {
            Movies savedMovie = moviesService.registerNewMovie(movie);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Movie created successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            throw new RuntimeException("Error creating movie", e);
        }
    }

    // Updated Movie
    @PutMapping("/updateMovie/{idMovie}")
    public ResponseEntity<Map<String, String>> updateMovie(@PathVariable Integer idMovie,
                                                           @RequestBody Movies updateMovie) {
        try {
            Movies movie = moviesService.updateMovie(idMovie, updateMovie);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Movie updated successfully");
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Deactivate Movie
    @PatchMapping("/deactivateMovie/{idMovie}")
    public ResponseEntity<Map<String, String>> deactivateUser(@PathVariable Integer idMovie){
        try {
            moviesService.deactivateMovie(idMovie);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Movie deactivated successfully");
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
