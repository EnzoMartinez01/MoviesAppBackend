package com.moviesapp.movies.Deserializer.Authentication;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.moviesapp.movies.Models.Authentication.Users;
import com.moviesapp.movies.Repositories.Authentication.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UsersDeserializer extends JsonDeserializer<Users> {
    @Autowired
    private UsersRepository usersRepository;

    @Override
    public Users deserialize(JsonParser p, DeserializationContext context) throws IOException, JsonProcessingException {
        Integer id = p.getIntValue();
        return usersRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
