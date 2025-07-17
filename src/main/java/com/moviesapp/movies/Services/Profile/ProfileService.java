package com.moviesapp.movies.Services.Profile;

import com.moviesapp.movies.Dto.Profile.ProfilesDto;
import com.moviesapp.movies.Models.Authentication.Users;
import com.moviesapp.movies.Models.Profiles.Profile;
import com.moviesapp.movies.Repositories.Authentication.UsersRepository;
import com.moviesapp.movies.Repositories.Profiles.ProfileRepository;
import com.moviesapp.movies.Services.Common.CommonService;
import com.moviesapp.movies.Services.Email.EmailService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfileService {
    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);

    private final UsersRepository usersRepository;
    private final ProfileRepository profileRepository;
    private final CommonService commonService;
    private final EmailService emailService;

    public ProfileService(UsersRepository usersRepository,
                          ProfileRepository profileRepository,
                          CommonService commonService,
                          EmailService emailService) {
        this.usersRepository = usersRepository;
        this.profileRepository = profileRepository;
        this.commonService = commonService;
        this.emailService = emailService;
    }

    // Get profiles by User
    public List<ProfilesDto> getProfilesByUser(Integer idUser) {
        Users user = usersRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return profileRepository.findByUsers(user)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    // Map To DTO for PROFILES
    public ProfilesDto mapToDto(Profile profile) {
        ProfilesDto profiles = new ProfilesDto();
        profiles.setProfileId(profile.getId());
        profiles.setNameProfile(profile.getNames());
        profiles.setUrlImageProfile(profile.getUrlImage());
        return profiles;
    }

    // Created new Profile for User
    @Transactional
    public Profile registerNewProfile(Profile profile) {
        try {
            Users users = commonService.authenticationCurrent();
            List<Profile> existingProfilesByUser = profileRepository.findByUsers(users);

            if (existingProfilesByUser.size() == 4 || existingProfilesByUser.size() > 4) {
                throw new RuntimeException("Excede el limite para registrar usuarios.");
            } else {
                profile.setUsers(users);
                emailService.sendProfileCreatedNotificationEmail(users.getEmail(), users.getFullname());

                logger.info("Profile {} created successfully", profile.getNames());
                return profileRepository.save(profile);
            }
        } catch (Exception e) {
            logger.error("Error creating profile", e);
            throw new RuntimeException("Error creating profile", e);
        }
    }

    // Updating a profile
    @Transactional
    public Profile updateProfile(Integer idProfile, Profile profile) {
        try {
            Profile existingProfile = profileRepository.findById(idProfile)
                    .orElseThrow(() -> new RuntimeException("Profile not found"));
            if (profile.getNames() != null && !profile.getNames().isEmpty()) {
                existingProfile.setNames(profile.getNames());
            }
            if (profile.getUrlImage() !=  null && !profile.getUrlImage().isEmpty()) {
                existingProfile.setUrlImage(profile.getUrlImage());
            }

            return profileRepository.save(existingProfile);
        } catch (Exception e) {
            logger.error("Error updating profile", e);
            throw new RuntimeException("Error updating profile", e);
        }
    }

    // Eliminated Profile
    public void eliminateProfile (Integer idProfile) {
        try {
            Profile profile = profileRepository.findById(idProfile)
                    .orElseThrow(() -> new RuntimeException("Profile not found"));

            profileRepository.delete(profile);
            logger.info("Profile deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleted profile", e);
            throw new RuntimeException("Error deleted profile", e);
        }
    }
}
