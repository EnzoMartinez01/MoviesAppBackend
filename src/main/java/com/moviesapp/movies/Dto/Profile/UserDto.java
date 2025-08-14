package com.moviesapp.movies.Dto.Profile;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserDto {
    private Integer idUser;
    private String names;
    private String lastnames;
    private String fullName;
    private String dni;
    private String telephone;
    private LocalDate birthDate;
    private String email;
    private String username;
    private String roleName;
    private Boolean isActive;
    List<ProfilesDto> profiles;
}
