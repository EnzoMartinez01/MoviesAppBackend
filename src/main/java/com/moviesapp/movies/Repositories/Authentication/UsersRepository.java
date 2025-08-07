package com.moviesapp.movies.Repositories.Authentication;

import com.moviesapp.movies.Models.Authentication.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);
    Optional<Users> findByUsername(String username);
    Optional<Users> findByVerificationCode(String verificationCode);
    
    @Query("""
        SELECT u FROM Users u
        WHERE (:searchTerm IS NULL OR 
               LOWER(u.names) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
               LOWER(u.lastnames) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
               LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
               LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
          AND (:isActive IS NULL OR u.isActive = :isActive)
    """)
    Page<Users> searchUsers(
            @Param("searchTerm") String searchTerm,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );
}
