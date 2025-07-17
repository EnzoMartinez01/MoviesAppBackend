package com.moviesapp.movies.Repositories.Authentication;

import com.moviesapp.movies.Models.Authentication.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);
    Optional<Users> findByUsername(String username);
    Optional<Users> findByVerificationCode(String verificationCode);
}
