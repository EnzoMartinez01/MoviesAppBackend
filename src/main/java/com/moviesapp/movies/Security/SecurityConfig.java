package com.moviesapp.movies.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(AuthenticationProvider authenticationProvider, JWTAuthenticationFilter jwtAuthenticationFilter) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/api/v1/auth/register/**",
                                "/api/v1/auth/login",
                                "/api/v1/auth/verify",
                                "/api/v1/auth/resend-code",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api/v1/auth/reset-password",
                                "/api/v1/auth/change-password",
                                "/api/v1/auth/update-password",
                                 //Products
                                "/api/v1/products/getProductsByFilters",
                                 "/api/v1/products/getProductsById/{productId}",
                                // Categories
                                "/api/v1/categories/getAllCategories",
                                "/api/v1/categories/getCategory/{categoryId}",
                                "/api/v1/categories/getCategoryByName/{name}"
                                // Brands
                                ,"/api/v1/brands/getAllBrands",
                                "/api/v1/brands/getAllBrandsImages",
                                //SubCategories
                                "/api/v1/subcategories/getAllSubCategories",
                                "/api/v1/subcategories/getSubCategory/",
                                "/api/v1/subcategories/products/subcategories",
                                // Attributes
                                "/api/v1/attributes/getAllAttributes",
                                "/api/v1/attributes/getAttributesByProducts/{idProduct}",
                                "/api/v1/attributes/getAttributesBySubCategory/{idSubCategory}",
                                // Characteristics
                                "/api/v1/characteristics/getAllCharacteristics",
                                "/api/v1/characteristics/getCharacteristicsByProducts/{idProduct}",
                                // FAQ
                                "/api/v1/faq/getAllFaq"
                        ).permitAll()
                        .requestMatchers("/api/v1/User/getAllUser").hasRole("ADMIN")
                        .anyRequest()
                        .authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:4200",
                "https://sxpv2242-4200.brs.devtunnels.ms",
                "http://localhost:57942"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Content-Disposition"));
        configuration.setExposedHeaders(Arrays.asList("Content-Disposition"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
