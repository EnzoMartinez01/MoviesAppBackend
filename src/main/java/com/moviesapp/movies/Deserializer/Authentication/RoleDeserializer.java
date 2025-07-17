package com.moviesapp.movies.Deserializer.Authentication;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.moviesapp.movies.Models.Authentication.Roles;
import com.moviesapp.movies.Repositories.Authentication.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RoleDeserializer extends JsonDeserializer<Roles> {
    @Autowired
    private RolesRepository rolesRepository;

    @Override
    public Roles deserialize(JsonParser p, DeserializationContext context) throws IOException, JsonProcessingException {
        Integer id = p.getIntValue();
        return rolesRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
    }
}
