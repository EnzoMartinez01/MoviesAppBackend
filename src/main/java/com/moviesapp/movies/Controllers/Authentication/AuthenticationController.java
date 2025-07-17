package com.moviesapp.movies.Controllers.Authentication;

import com.moviesapp.movies.Dto.Authentication.LoginDto;
import com.moviesapp.movies.Models.Authentication.CustomUserDetails;
import com.moviesapp.movies.Models.Authentication.Users;
import com.moviesapp.movies.Repositories.Authentication.RolesRepository;
import com.moviesapp.movies.Security.JWTService;
import com.moviesapp.movies.Services.Authentication.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JWTService jwtService;
    private final RolesRepository roleRepository;

    public AuthenticationController(AuthenticationService authenticationService,
                                    JWTService jwtService,
                                    RolesRepository roleRepository) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.roleRepository = roleRepository;
    }

    // Register User
    @PostMapping("/register/user/{idRole}")
    public ResponseEntity<Map<String, String>> registerUser(
            @PathVariable Integer idRole,
            @RequestBody Users users) {
        try {
            if (users.getEmail() == null || users.getPassword() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email y contraseña son requeridos."));
            }

            if (!roleRepository.existsById(idRole)) {
                return ResponseEntity.badRequest().body(Map.of("message", "El rol con ID " + idRole + " no existe."));
            }

            authenticationService.registerUser(users, idRole);

            return ResponseEntity.ok().body(Map.of("message", "Usuario Registrado Satisfactoriamente.", "email", users.getEmail()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error interno al registrar usuario."));
        }
    }


    //Resend Verification Code
    @PostMapping("/resend-code")
    public ResponseEntity<String> resendVerificationCode(@RequestParam String email) {
        try {
            boolean isResent = authenticationService.resendVerificationCode(email);
            if (isResent) {
                return ResponseEntity.ok("El código de verificación ha sido enviado nuevamente.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró un usuario con el correo proporcionado.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al reenviar el código de verificación.");
        }
    }

    //Verify User
    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyUser(@RequestParam String email, @RequestParam String code) {
        try {
            boolean verified = authenticationService.verifyUsers(email, code);
            return verified ? ResponseEntity.ok(Map.of("message", "Usuario verificado exitosamente.")) : ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Verification failed or code expired."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error verifying personal."));        }
    }

    // Reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestParam String email) {
        try {
            boolean resent = authenticationService.resendPasswordResetVerificationCode(email);
            if (resent) {
                return ResponseEntity.ok(Map.of("message", "El código de verificación ha sido enviado nuevamente."));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "No se encontró un usuario con el correo proporcionado."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al enviar el código de verificación."));
        }
    }

    //Change Password
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestParam String code, @RequestParam String newPassword) {
        try {
            String message = authenticationService.changePassword(code, newPassword);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al cambiar la contraseña."));
        }
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Serializable>> authenticateUser(@RequestBody LoginDto loginDto) {
        try {
            Users authenticatedPersonal = authenticationService.authenticate(loginDto);

            if (!authenticatedPersonal.getIsActive()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Cuenta inactiva. Contacte con el administrador."));
            }

            String role = (authenticatedPersonal.getRole() != null) ? authenticatedPersonal.getRole().getName() : "ROLE_USER";
            Integer idUser = authenticatedPersonal.getId() != null ? authenticatedPersonal.getId() : 0;
            String jwtToken = jwtService.generateToken(new CustomUserDetails(authenticatedPersonal));

            return ResponseEntity.ok(Map.of(
                    "token", jwtToken,
                    "user", idUser,
                    "role", role,
                    "message", "Inicio de sesión exitoso!"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credenciales incorrectas o usuario no encontrado."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error interno al procesar la solicitud."));
        }
    }

    //Logout
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token no proporcionado o inválido."));
        }

        String token = authHeader.substring(7);

        authenticationService.logout(token);

        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }
}
