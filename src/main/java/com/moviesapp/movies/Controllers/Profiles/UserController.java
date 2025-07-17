package com.moviesapp.movies.Controllers.Profiles;

import com.moviesapp.movies.Dto.Profile.UserDto;
import com.moviesapp.movies.Models.Authentication.Users;
import com.moviesapp.movies.Services.Profile.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UsersService usersService;

    public UserController(UsersService usersService){
        this.usersService = usersService;
    }

    //Get Current User
    @GetMapping("/me")
    public ResponseEntity<Optional<UserDto>> getUser() {
        try {
            Optional<UserDto> user = usersService.getCurrentUser();
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //Updated User
    @PutMapping("/updateUser/{idUser}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable Integer idUser,
                                                          @RequestBody Users updateUser) {
        try {
            Users user = usersService.updateUser(idUser, updateUser);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User updated successfully");
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //Deactivate User
    @PatchMapping("/deactivateUser/{idUser}")
    public ResponseEntity<Map<String, String>> deactivateUser(@PathVariable Integer idUser){
        try {
            usersService.deactivateUser(idUser);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deactivated successfully");
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
