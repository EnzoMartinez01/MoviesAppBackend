package com.moviesapp.movies.Services.Experience;

import com.moviesapp.movies.Dto.Experience.WatchListDto;
import com.moviesapp.movies.Dto.Movies.MoviesDto;
import com.moviesapp.movies.Models.Authentication.Users;
import com.moviesapp.movies.Models.Experience.Watchlist;
import com.moviesapp.movies.Models.Movies.Movies;
import com.moviesapp.movies.Repositories.Authentication.UsersRepository;
import com.moviesapp.movies.Repositories.Experience.WatchlistRepository;
import com.moviesapp.movies.Repositories.Movies.MoviesRepository;
import com.moviesapp.movies.Services.Common.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class WatchlistService {
    private static final Logger logger = LoggerFactory.getLogger(WatchlistService.class);

    private final WatchlistRepository watchlistRepository;
    private final UsersRepository usersRepository;
    private final MoviesRepository moviesRepository;
    private final CommonService commonService;

    public WatchlistService (WatchlistRepository watchlistRepository,
                             UsersRepository usersRepository,
                             MoviesRepository moviesRepository,
                             CommonService commonService) {
        this.watchlistRepository = watchlistRepository;
        this.moviesRepository = moviesRepository;
        this.commonService = commonService;
        this.usersRepository = usersRepository;
    }

    // Get all WatchList
    public Page<WatchListDto> getAllWatchList(int page, int size) {
        Page<Watchlist> watchlist = watchlistRepository.findAll(PageRequest.of(page, size));
        return watchlist.map(this::mapToDto);
    }

    // Get WatchList by User
    public Page<WatchListDto> getWatchListByUser(int page, int size) {
        var user = commonService.authenticationCurrent();
        Page<Watchlist> watchlist = watchlistRepository.findByUser(user, PageRequest.of(page, size));
        return watchlist.map(this::mapToDto);
    }

    // Map To Dto
    public WatchListDto mapToDto (Watchlist watchlist) {
        WatchListDto dto = new WatchListDto();

        dto.setIdWatchlist(watchlist.getId());
        dto.setFullname(watchlist.getUser().getFullname());
        dto.setUsername(watchlist.getUser().getUsername());
        dto.setListName(watchlist.getListName());
        List<MoviesDto> moviesDtos = (watchlist.getMovies() != null) ?
                watchlist.getMovies().stream().map(this::mapToMoviesDto).toList() : Collections.emptyList();
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

    // Create a new Watchlist
    @Transactional
    public Watchlist addWatchlist(String listName) {
        try {
            Users user = commonService.authenticationCurrent();
            
            if (watchlistRepository.existsByUserAndListName(user, listName)) {
                throw new RuntimeException("Watchlist with name '" + listName + "' already exists");
            }
            
            Watchlist watchlist = new Watchlist();
            watchlist.setUser(user);
            watchlist.setListName(listName);
            watchlist.setMovies(new ArrayList<>());
            
            return watchlistRepository.save(watchlist);
        } catch (Exception e) {
            logger.error("Error creating watchlist: {}", e.getMessage());
            throw new RuntimeException("Failed to create watchlist: " + e.getMessage());
        }
    }
    
    // Add movie to watchlist
    @Transactional
    public Watchlist addMovieToWatchlist(Integer watchlistId, Integer movieId) {
        try {
            Users user = commonService.authenticationCurrent();
            
            Watchlist watchlist = watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new RuntimeException("Watchlist not found"));
                
            if (!watchlist.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Access denied to this watchlist");
            }
            
            Movies movie = moviesRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
                
            if (!watchlist.getMovies().contains(movie)) {
                watchlist.getMovies().add(movie);
                return watchlistRepository.save(watchlist);
            }
            
            return watchlist;
        } catch (Exception e) {
            logger.error("Error adding movie to watchlist: {}", e.getMessage());
            throw new RuntimeException("Failed to add movie to watchlist: " + e.getMessage());
        }
    }
    
    // Delete watchlist
    @Transactional
    public void deleteWatchlist(Integer watchlistId) {
        try {
            Users user = commonService.authenticationCurrent();
            
            Watchlist watchlist = watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new RuntimeException("Watchlist not found"));
                
            if (!watchlist.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Access denied to this watchlist");
            }
            
            watchlistRepository.delete(watchlist);
        } catch (Exception e) {
            logger.error("Error deleting watchlist: {}", e.getMessage());
            throw new RuntimeException("Failed to delete watchlist: " + e.getMessage());
        }
    }
}
