package com.moviesapp.movies.Repositories.Profiles;

import com.moviesapp.movies.Models.Authentication.Users;
import com.moviesapp.movies.Models.Profiles.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    List<Profile> findByUsers(Users user);
}
