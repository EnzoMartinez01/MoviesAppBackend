package com.moviesapp.movies.Controllers.Movies;

import com.moviesapp.movies.Dto.Movies.GenreDto;
import com.moviesapp.movies.Models.Movies.Genre;
import com.moviesapp.movies.Services.Movies.GenreService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/genre")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    // Get All Genres
    @GetMapping("/getAll")
    public Page<GenreDto> getGenres(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        return genreService.getAllGenres(page, size);
    }

    // Get Genre by ID
    @GetMapping("/getGenre/{idGenre}")
    public GenreDto getGenreById(@PathVariable Integer idGenre) {
        return genreService.getGenreById(idGenre);
    }

    // Create a new Genre
    @PostMapping("/addGenre")
    public ResponseEntity<Map<String, String>> addGenre(@RequestBody Genre genre) {
        try {
            Genre savedGenre = genreService.registerNewGenre(genre);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Genre created successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e){
            throw new RuntimeException("Error creating genre", e);
        }
    }

    // Updated Genre
    @PutMapping("/updateGenre/{idGenre}")
    public ResponseEntity<Map<String, String>> updateGenre(@PathVariable Integer idGenre,
                                                           @RequestBody Genre updateGenre) {
        try {
            Genre genre = genreService.updateGenre(idGenre, updateGenre);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Genre updated successfully");
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
