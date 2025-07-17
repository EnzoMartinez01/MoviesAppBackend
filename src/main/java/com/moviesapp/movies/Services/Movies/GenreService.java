package com.moviesapp.movies.Services.Movies;

import com.moviesapp.movies.Dto.Movies.GenreDto;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class GenreService {
    private static final Logger logger = LoggerFactory.getLogger(GenreService.class);

    private final GenreRepository genreRepository;
    private final CommonService commonService;
    private final MoviesRepository moviesRepository;

    public GenreService (GenreRepository genreRepository,
                         CommonService commonService,
                         MoviesRepository moviesRepository) {
        this.genreRepository = genreRepository;
        this.commonService = commonService;
        this.moviesRepository = moviesRepository;
    }

    // Get All genres
    public Page<GenreDto> getAllGenres(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Genre> genre = genreRepository.findAll(pageable);
        return genre.map(this::mapToDto);
    }

    // Get Genre by ID
    public GenreDto getGenreById(Integer idGenre) {
        Genre genre = genreRepository.findById(idGenre)
                .orElseThrow(() -> new RuntimeException("Genre not found"));
        return mapToDto(genre);
    }

    // Map to DTO for Genres
    public GenreDto mapToDto(Genre genre) {
        GenreDto dto = new GenreDto();
        dto.setIdGenre(genre.getId());
        dto.setGenreName(genre.getName());
        List<MoviesDto> moviesDtos = (genre.getMovies() != null) ?
                genre.getMovies().stream().map(this::mapToMoviesDto).toList() : Collections.emptyList();
        dto.setMovies(moviesDtos);
        return dto;
    }

    // Map to DTO for Movies
    public MoviesDto mapToMoviesDto(Movies movies) {
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
        dto.setUrlTrailer(movies.getUrlTrailer());
        dto.setMovieUrl(movies.getUrlMovie());
        return dto;
    }

    // Create a new Genre
    @Transactional
    public Genre registerNewGenre(Genre genre) {
        try {
            logger.info("Genre {} created successfully", genre.getName());
            return genreRepository.save(genre);
        } catch (Exception e) {
            logger.error("Error creating genre", e);
            throw new RuntimeException("Error creating genre", e);
        }
    }

    // Updating Genre
    @Transactional
    public Genre updateGenre(Integer idGenre, Genre genre) {
        try {
            Genre existingGenre = genreRepository.findById(idGenre)
                    .orElseThrow(() -> new RuntimeException("Genre not found"));

            if (genre.getName() != null && !genre.getName().isEmpty()) {
                existingGenre.setName(genre.getName());
            }

            return genreRepository.save(existingGenre);
        } catch (Exception e) {
            logger.error("Error updating genre", e);
            throw new RuntimeException("Error updating genre", e);
        }
    }
}
