package com.moviesapp.movies.Deserializer.Movies;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.moviesapp.movies.Models.Movies.Genre;
import com.moviesapp.movies.Repositories.Movies.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GenreDeserializer extends JsonDeserializer<Genre> {
    @Autowired
    private GenreRepository genreRepository;

    @Override
    public Genre deserialize(JsonParser p, DeserializationContext context) throws IOException, JsonProcessingException {
        Integer id = p.getIntValue();
        return genreRepository.findById(id).orElseThrow(() -> new RuntimeException("Genre not found"));
    }
}
