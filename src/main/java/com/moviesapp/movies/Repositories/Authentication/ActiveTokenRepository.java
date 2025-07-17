package com.moviesapp.movies.Repositories.Authentication;

import com.moviesapp.movies.Models.Authentication.ActiveToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActiveTokenRepository extends JpaRepository<ActiveToken, Long> {
    Optional<ActiveToken> findByToken(String token);
    void deleteByToken(String token);
}
