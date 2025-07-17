package com.moviesapp.movies.Repositories.Experience;

import com.moviesapp.movies.Models.Authentication.Users;
import com.moviesapp.movies.Models.Experience.Watchlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Integer> {
    Page<Watchlist> findByUser(Users user, Pageable pageable);
    List<Watchlist> findByUserAndListName(Users user, String listName);
    boolean existsByUserAndListName(Users user, String listName);
}
