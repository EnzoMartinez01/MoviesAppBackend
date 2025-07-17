package com.moviesapp.movies.Controllers.Profiles;

import com.moviesapp.movies.Dto.Profile.ProfilesDto;
import com.moviesapp.movies.Models.Profiles.Profile;
import com.moviesapp.movies.Services.Profile.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController (ProfileService profileService) {
        this.profileService = profileService;
    }

    // Get Profile by User
    @GetMapping("/getProfiles/{idUser}")
    public ResponseEntity<List<ProfilesDto>> getProfiles(@PathVariable Integer idUser) {
        try {
            List<ProfilesDto> profiles = profileService.getProfilesByUser(idUser);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    // Create new Profiles
    @PostMapping("/createProfile")
    public ResponseEntity<Map<String, String>> addProfile (@RequestBody Profile profile) {
        try {
            Profile savedProfile = profileService.registerNewProfile(profile);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Profile created successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Updated Profile
    @PutMapping("/updatedProfile/{idProfile}")
    public ResponseEntity<Map<String, String>> updatedProfile (@PathVariable Integer idProfile,
                                                               @RequestBody Profile updatedProfile) {
        try {
            Profile profile = profileService.updateProfile(idProfile, updatedProfile);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Profile updated successfully");
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Deleted Profile
    @DeleteMapping("/deleteProfile/{idProfile}")
    public ResponseEntity<Map<String, String>> deleteProfile(@PathVariable Integer idProfile) {
        try {
            profileService.eliminateProfile(idProfile);
            Map<String, String> response =new HashMap<>();
            response.put("message", "Profile deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}