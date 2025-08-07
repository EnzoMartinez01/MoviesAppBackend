package com.moviesapp.movies.Services.Movies;

import com.moviesapp.movies.Dto.Movies.MoviesDto;
import com.moviesapp.movies.Models.Movies.Genre;
import com.moviesapp.movies.Models.Movies.Movies;
import com.moviesapp.movies.Repositories.Movies.GenreRepository;
import com.moviesapp.movies.Repositories.Movies.MoviesRepository;
import com.moviesapp.movies.Services.Common.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MoviesService {
    private static final Logger logger = LoggerFactory.getLogger(MoviesService.class);

    private final MoviesRepository moviesRepository;
    private final GenreRepository genreRepository;
    private final CommonService commonService;

    public MoviesService(MoviesRepository moviesRepository,
                         GenreRepository genreRepository,
                         CommonService commonService) {
        this.moviesRepository = moviesRepository;
        this.genreRepository = genreRepository;
        this.commonService = commonService;
    }

    // Get Movies by Filters
    public Page<MoviesDto> searchMoviesWithFilters(String searchTerm,
                                                String language,
                                                String ranking,
                                                Boolean isActive,
                                                LocalDate releaseDateStart,
                                                LocalDate releaseDateEnd,
                                                Pageable pageable) {
        return moviesRepository.searchMoviesWithFilters(
                searchTerm, language, ranking, isActive, releaseDateStart, releaseDateEnd, pageable
        ).map(this::mapToDto);
    }

    // Get All Movies
    public Page<MoviesDto> getAllMovies(int page, int size, Boolean isActive) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movies> movies = moviesRepository.findByIsActive(pageable, isActive);
        return movies.map(this::mapToDto);
    }

    // Get Movie by ID
    public MoviesDto getMovieById(Integer idMovie) {
        Movies movie = moviesRepository.findById(idMovie)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        return mapToDto(movie);
    }

    // Get Movie by Genre
    public Page<MoviesDto> getMovieByGenre(int page, int size,
                                           Integer idGenre, Boolean isActive) {
        Pageable pageable = PageRequest.of(page, size);

        Genre genre = null;
        if (idGenre != null) {
            genre = genreRepository.findById(idGenre)
                    .orElseThrow(() -> new RuntimeException("Genre not found"));
        }

        Page<Movies> moviesPage = moviesRepository.findByGenreAndIsActive(genre, isActive, pageable);
        return moviesPage.map(this::mapToDto);
    }


    public Page<MoviesDto> getTopRatedMovies(boolean isActive, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movies> movies = moviesRepository.findTopRatedMoviesByActive(isActive, pageable);
        return movies.map(this::mapToDto);
    }

    // Map to DTO for Movies
    public MoviesDto mapToDto(Movies movies) {
        MoviesDto dto = new MoviesDto();
        dto.setIdMovie(movies.getId());
        dto.setTitleMovie(movies.getTitle());
        dto.setSynopsisMovie(movies.getSynopsis());
        dto.setGenreName(movies.getGenre().getName());
        dto.setPg(movies.getRanking());
        dto.setLanguage(movies.getLanguage());
        dto.setDuration(movies.getDurationInMinutes());
        dto.setReleaseDate(movies.getReleaseDate());
        dto.setMovieImage(movies.getCoverImage());
        dto.setMovieUrl(movies.getUrlMovie());
        dto.setUrlTrailer(movies.getUrlTrailer());
        return dto;
    }

    // Create a new Movie
    @Transactional
    public Movies registerNewMovie(Movies movies) {
        try {
            if (movies.getGenre() == null || movies.getGenre().getId() == null) {
                throw new IllegalArgumentException("Genre ID must be provided");
            }

            Genre genre = genreRepository.findById(movies.getGenre().getId())
                    .orElseThrow(() -> new RuntimeException("Genre not found"));

            movies.setGenre(genre);
            movies.setIsActive(true);
            Movies savedMovie = moviesRepository.save(movies);
            logger.info("Movie '{}' created successfully with ID {}", savedMovie.getTitle(), savedMovie.getId());
            return savedMovie;
        } catch (Exception e){
            logger.error("Error creating movie", e);
            throw new RuntimeException("Error creating movie", e);
        }
    }

    // Updated Movie
    public Movies updateMovie(Integer idMovie, Movies movies) {
        try {
            Movies existingMovie = moviesRepository.findById(idMovie)
                    .orElseThrow(() -> new RuntimeException("Movie not found"));

            if (movies.getTitle() != null && !movies.getTitle().isEmpty()) {
                existingMovie.setTitle(movies.getTitle());
            }
            if (movies.getSynopsis() != null && !movies.getSynopsis().isEmpty()) {
                existingMovie.setSynopsis(movies.getSynopsis());
            }
            if (movies.getRanking() != null) {
                existingMovie.setRanking(movies.getRanking());
            }
            if (movies.getLanguage() != null && !movies.getLanguage().isEmpty()) {
                existingMovie.setLanguage(movies.getLanguage());
            }
            if (movies.getDurationInMinutes() != null) {
                existingMovie.setDurationInMinutes(movies.getDurationInMinutes());
            }
            if (movies.getReleaseDate() != null) {
                existingMovie.setReleaseDate(movies.getReleaseDate());
            }
            if (movies.getCoverImage() != null && !movies.getCoverImage().isEmpty()) {
                existingMovie.setCoverImage(movies.getCoverImage());
            }
            if (movies.getUrlTrailer() != null && !movies.getUrlTrailer().isEmpty()) {
                existingMovie.setUrlTrailer(movies.getUrlTrailer());
            }
            if (movies.getGenre() != null) {
                Genre genre = genreRepository.findById(movies.getGenre().getId())
                        .orElseThrow(() -> new RuntimeException("Genre not found"));
                existingMovie.setGenre(genre);
            }
            if (movies.getUrlMovie() != null && !movies.getUrlMovie().isEmpty()){
                existingMovie.setUrlMovie(movies.getUrlMovie());
            }
            if (movies.getIsActive() != null) {
                existingMovie.setIsActive(movies.getIsActive());
            }
            return moviesRepository.save(existingMovie);
        } catch (Exception e) {
            logger.error("Error updating movie", e);
            throw new RuntimeException("Error updating movie", e);
        }
    }

    // Deactivate Movie
    public Movies deactivateMovie(Integer idMovie) {
        try {
            Movies existingMovie = moviesRepository.findById(idMovie)
                    .orElseThrow(() -> new RuntimeException("Movie not found"));
            existingMovie.setIsActive(false);
            return moviesRepository.save(existingMovie);
        } catch (Exception e) {
            logger.error("Error deactivating movie", e);
            throw new RuntimeException("Error deactivating movie", e);
        }
    }
}
