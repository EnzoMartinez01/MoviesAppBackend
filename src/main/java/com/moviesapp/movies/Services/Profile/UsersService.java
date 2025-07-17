package com.moviesapp.movies.Services.Profile;

import com.moviesapp.movies.Dto.Profile.ProfilesDto;
import com.moviesapp.movies.Dto.Profile.UserDto;
import com.moviesapp.movies.Models.Authentication.Users;
import com.moviesapp.movies.Models.Profiles.Profile;
import com.moviesapp.movies.Repositories.Authentication.UsersRepository;
import com.moviesapp.movies.Services.Common.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService {
    private final static Logger logger = LoggerFactory.getLogger(UsersService.class);

    private final UsersRepository usersRepository;
    private final CommonService commonService;

    public UsersService (UsersRepository usersRepository,
                         CommonService commonService) {
        this.usersRepository = usersRepository;
        this.commonService = commonService;
    }

    public Optional<UserDto> getCurrentUser() {
        Users user = commonService.authenticationCurrent();

        return usersRepository.findByUsername(user.getUsername())
                .map(this::mapToDto);
    }

    // Map to DTO for Users
    public UserDto mapToDto(Users users) {
        UserDto dto = new UserDto();
        dto.setIdUser(users.getId());
        dto.setNames(users.getNames());
        dto.setLastnames(users.getLastnames());
        dto.setDni(users.getDni());
        dto.setTelephone(users.getTelephone());
        dto.setEmail(users.getEmail());
        dto.setUsername(users.getUsername());
        dto.setBirthDate(users.getBirthDate());
        dto.setFullName(users.getFullname());
        List<ProfilesDto> profilesDtos = (users.getProfiles() != null) ?
                users.getProfiles().stream().map(this::mapToProfilesDto).toList() : Collections.emptyList();
        dto.setProfiles(profilesDtos);
        return dto;
    }

    // Map to DTO for Profiles
    public ProfilesDto mapToProfilesDto(Profile profile) {
        ProfilesDto profiles = new ProfilesDto();
        profiles.setProfileId(profile.getId());
        profiles.setNameProfile(profile.getNames());
        profiles.setUrlImageProfile(profile.getUrlImage());
        return profiles;
    }

    @Transactional
    public Users updateUser(Integer idUser, Users user) {
        try {
            Users existingUser = usersRepository.findById(idUser)
                    .orElseThrow(() -> new RuntimeException("User not found."));

            if (user.getNames() != null && !user.getNames().isEmpty()) {
                existingUser.setNames(user.getNames());
            }
            if (user.getLastnames() != null && !user.getLastnames().isEmpty()) {
                existingUser.setLastnames(user.getLastnames());
            }
            if (user.getDni() != null && !user.getDni().isEmpty()) {
                existingUser.setDni(user.getDni());
            }
            if (user.getTelephone() != null && !user.getTelephone().isEmpty()) {
                existingUser.setTelephone(user.getTelephone());
            }
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                existingUser.setEmail(user.getEmail());
            }
            if (user.getBirthDate() != null) {
                existingUser.setBirthDate(user.getBirthDate());
            }
            if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                existingUser.setUsername(user.getUsername());
            }
            if (user.getRole() != null) {
                existingUser.setRole(user.getRole());
            }
            if (user.getIsActive() != null) {
                existingUser.setIsActive(user.getIsActive());
            }
            if ((user.getNames() != null && !user.getNames().isEmpty())
                    || (user.getLastnames() != null && !user.getLastnames().isEmpty())) {
                existingUser.setFullname(
                        (user.getNames() != null ? user.getNames() : existingUser.getNames()) + " " +
                                (user.getLastnames() != null ? user.getLastnames() : existingUser.getLastnames())
                );
            }

            return usersRepository.save(existingUser);
        } catch (Exception e){
            logger.error("Error updating user", e);
            throw new RuntimeException("Error updating user", e);
        }
    }

    // Deactivate User by ID
    public void deactivateUser(Integer idUser) {
        Users user = usersRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + idUser));

        if (!user.getIsActive()) {
            throw new IllegalStateException("User is already deactivated.");
        }

        user.setIsActive(false);
        usersRepository.save(user);
    }
}
