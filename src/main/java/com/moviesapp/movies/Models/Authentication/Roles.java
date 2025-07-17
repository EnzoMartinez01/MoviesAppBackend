package com.moviesapp.movies.Models.Authentication;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "dbo_roles")
@Data
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
}
