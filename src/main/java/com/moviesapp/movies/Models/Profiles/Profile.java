package com.moviesapp.movies.Models.Profiles;

import com.moviesapp.movies.Models.Authentication.Users;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "t_profiles")
@Data
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String names;

    private String urlImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Users users;
}
