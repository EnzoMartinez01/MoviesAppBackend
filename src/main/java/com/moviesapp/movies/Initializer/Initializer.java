package com.moviesapp.movies.Initializer;

import com.moviesapp.movies.Models.Authentication.Roles;
import com.moviesapp.movies.Repositories.Authentication.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Initializer implements CommandLineRunner {
    @Autowired
    private RolesRepository rolesRepository;

    @Override
    public void run(String... args) throws Exception {
        //Inserción de datos para Roles
        inicializeRole("ADMIN");
        inicializeRole("MODERADOR");
        inicializeRole("USUARIO");
    }

    private void inicializeRole(String roleName) {
        Optional<Roles> existingRole = rolesRepository.findByName(roleName);
        if (existingRole.isEmpty()) {
            Roles role = new Roles();
            role.setName(roleName);
            rolesRepository.save(role);
        }
    }
}
