package com.moviesapp.movies.Controllers.Experience;

import com.moviesapp.movies.Dto.Experience.WatchListDto;
import com.moviesapp.movies.Models.Experience.Watchlist;
import com.moviesapp.movies.Services.Experience.WatchlistService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/watchlist")
public class WatchlistController {
    private final WatchlistService watchlistService;

    public WatchlistController (WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    // Get All watchlist
    @GetMapping("/getAll")
    public Page<WatchListDto> getAllWatchlist(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        return watchlistService.getAllWatchList(page, size);
    }

    // Get Watchlist by User
    @GetMapping("/myWatchlist")
    public Page<WatchListDto> getWatchlistByUser(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return watchlistService.getWatchListByUser(page, size);
    }

    // Create watchlist
    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createWatchlist(@RequestParam String listName) {
        try {
            Watchlist watchlist = watchlistService.addWatchlist(listName);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Watchlist created successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            throw new RuntimeException("Error creating watchlist", e);
        }
    }

    // Add movie to watchlist
    @PutMapping("/addMovie")
    public ResponseEntity<Map<String, String>> addMovieToWatchlist(@RequestParam Integer idMovie,
                                         @RequestParam Integer idWatchlist) {
        try {
            Watchlist watchlist = watchlistService.addMovieToWatchlist(idWatchlist, idMovie);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Movie added to watchlist successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            throw new RuntimeException("Error adding movie to watchlist", e);
        }
    }

    // Delete watchlist
    @DeleteMapping("/deletewatchlist/{idWatchlist}")
    public ResponseEntity<Map<String, String>> deleteWatchlist (@PathVariable Integer idWatchlist) {
        try {
            watchlistService.deleteWatchlist(idWatchlist);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Watchlist eliminated successfully");
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
