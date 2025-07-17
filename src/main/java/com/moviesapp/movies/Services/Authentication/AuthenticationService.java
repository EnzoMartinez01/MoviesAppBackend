package com.moviesapp.movies.Services.Authentication;

import com.moviesapp.movies.Dto.Authentication.LoginDto;
import com.moviesapp.movies.Models.Authentication.Roles;
import com.moviesapp.movies.Models.Authentication.Users;
import com.moviesapp.movies.Repositories.Authentication.ActiveTokenRepository;
import com.moviesapp.movies.Repositories.Authentication.RolesRepository;
import com.moviesapp.movies.Repositories.Authentication.UsersRepository;
import com.moviesapp.movies.Security.JWTService;
import com.moviesapp.movies.Services.Email.EmailService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RolesRepository rolesRepository;
    private final EmailService emailService;
    private final ActiveTokenRepository activeTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public AuthenticationService(UsersRepository usersRepository,
                                 BCryptPasswordEncoder passwordEncoder,
                                 RolesRepository rolesRepository,
                                 EmailService emailService,
                                 ActiveTokenRepository activeTokenRepository,
                                 AuthenticationManager authenticationManager,
                                 JWTService jwtService) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolesRepository = rolesRepository;
        this.emailService = emailService;
        this.activeTokenRepository = activeTokenRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // Register User
    public void registerUser(Users users, Integer idRole) {
        try {
            users.setPassword(passwordEncoder.encode(users.getPassword()));

            Roles role = rolesRepository.findById(idRole)
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + idRole));
            users.setRole(role);

            users.setCreated_at(LocalDate.now());
            users.setIsActive(true);
            users.setFullname(users.getNames() + " " + users.getLastnames());

            Users savedUsers = usersRepository.save(users);
            logger.info("User " + savedUsers.getUsername() + " created successfully");

            //sendVerificationCode(savedUsers);

        } catch (Exception e) {
            logger.error("Error creando usuario", e);
            throw new RuntimeException("Error creando usuario", e);
        }
    }

    // Send Verification Code
    public void sendVerificationCode(Users users) {
        Random random = new Random();
        String code = String.format("%06d", random.nextInt(1000000));
        users.setVerificationCode(code);
        users.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(2));
        usersRepository.save(users);
        emailService.sendVerificationEmail(users.getEmail(), code, users.getFullname());
    }

    // Re-send verification code
    public boolean resendVerificationCode(String email) {
        Optional<Users> userOptional = usersRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();

            sendVerificationCode(user);
            return true;
        }
        return false;
    }

    // Send Reset Password Verification Code
    public void sendResetPasswordVerificationCode(Users users) {
        Random random = new Random();
        String code = String.format("%06d", random.nextInt(1000000));
        users.setVerificationCode(code);
        users.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(10));
        usersRepository.save(users);
        emailService.sendPasswordResetEmail(users.getEmail(), code, users.getFullname());
    }

    // Re-send password reset email
    public boolean resendPasswordResetVerificationCode(String email) {
        Optional<Users> userOptional = usersRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();

            sendResetPasswordVerificationCode(user);
            return true;
        }
        return false;
    }

    public String changePassword(String code, String newPassword) {
        try {
            Users user = usersRepository.findByVerificationCode(code)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código inválido"));

            if (user.getVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código expirado");
            }

            user.addPasswordToHistory(user.getPassword());
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setVerificationCode(null);
            user.setVerificationCodeExpiry(null);
            usersRepository.save(user);

            return "Contraseña actualizada correctamente.";
        } catch (Exception e) {
            logger.error("Error al cambiar la contraseña", e);
            throw new RuntimeException("Error al cambiar la contraseña", e);
        }
    }

    // Verification Code
    @Transactional
    public boolean verifyUsers(String email, String code) {
        Optional<Users> personalOptional = usersRepository.findByEmail(email);

        if (personalOptional.isPresent()) {
            Users users = personalOptional.get();
            if (users.getVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
                usersRepository.delete(users);
                return false;
            }
            if (users.getVerificationCode().equals(code) && users.getVerificationCodeExpiry().isAfter(LocalDateTime.now())) {
                users.setIsActive(true);
                users.setVerificationCode(null);
                users.setVerificationCodeExpiry(null);
                usersRepository.save(users);
                return true;
            }
        }
        return false;
    }

    // Authentication
    public Users authenticate(LoginDto loginDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );

        Users users = usersRepository.findByUsername(loginDto.getUsername()).orElseThrow();
        usersRepository.save(users);

        return users;
    }

    //Logout
    @Transactional
    public void logout(String token) {
        activeTokenRepository.deleteByToken(token);
        System.out.println("Token eliminado correctamente.");
    }
}
