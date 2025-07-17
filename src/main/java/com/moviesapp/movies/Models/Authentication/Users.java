package com.moviesapp.movies.Models.Authentication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.moviesapp.movies.Deserializer.Authentication.RoleDeserializer;
import com.moviesapp.movies.Models.Profiles.Profile;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_users")
@Data
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;
    @Column(nullable = false)
    private String names;
    @Column(nullable = false)
    private String lastnames;
    @Column(nullable = false, unique = true)
    @Size(min = 7)
    private String dni;
    @Size(min = 9)
    private String telephone;
    private String fullname;
    @Column(nullable = false, unique = true)
    private String email;
    private LocalDate birthDate;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String username;
    private LocalDate created_at;
    private Boolean isActive;

    @OneToMany(mappedBy = "users", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Profile> profiles;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    @JsonDeserialize(using = RoleDeserializer.class)
    private Roles role;

    //Historial de Contraseñas
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private List<PasswordHistory> passwordHistory = new ArrayList<>();

    public void addPasswordToHistory(String oldPassword) {
        PasswordHistory history = new PasswordHistory();
        history.setPassword(oldPassword);
        history.setUsers(this);
        passwordHistory.add(history);
    }

    //Verificación Correo
    private String verificationCode;
    private LocalDateTime verificationCodeExpiry;
}
