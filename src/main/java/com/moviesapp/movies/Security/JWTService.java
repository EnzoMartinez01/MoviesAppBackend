package com.moviesapp.movies.Security;

import com.moviesapp.movies.Models.Authentication.ActiveToken;
import com.moviesapp.movies.Models.Authentication.Users;
import com.moviesapp.movies.Repositories.Authentication.ActiveTokenRepository;
import com.moviesapp.movies.Repositories.Authentication.UsersRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JWTService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    private final Set<String> blacklist = new HashSet<>();

    private final ActiveTokenRepository activeTokenRepository;
    private final UsersRepository usersRepository;

    public JWTService(ActiveTokenRepository activeTokenRepository, UsersRepository usersRepository) {
        this.activeTokenRepository = activeTokenRepository;
        this.usersRepository = usersRepository;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public void addToBlacklist(String token) {
        blacklist.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklist.contains(token);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        List<String> permissions = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        extraClaims.put("permissions", permissions);

        String token = buildToken(extraClaims, userDetails);
        saveToken(token, userDetails.getUsername());
        return token;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean isActive = activeTokenRepository.findByToken(token).isPresent();
        return (username.equals(userDetails.getUsername())) && isActive;
    }

    public void invalidateToken(String token) {
        activeTokenRepository.deleteByToken(token);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private void saveToken(String token, String username) {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username not found."));
        ActiveToken activeToken = new ActiveToken();
        activeToken.setToken(token);
        activeToken.setUser(user);
        activeToken.setConnectionDate(LocalDateTime.now());
        activeTokenRepository.save(activeToken);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
}
