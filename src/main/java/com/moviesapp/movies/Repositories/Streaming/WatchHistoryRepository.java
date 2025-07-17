package com.moviesapp.movies.Repositories.Streaming;

import com.moviesapp.movies.Models.Streaming.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Integer> {
}
