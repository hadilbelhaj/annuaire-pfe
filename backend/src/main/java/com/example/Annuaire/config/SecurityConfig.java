package com.example.Annuaire.config;

import com.example.Annuaire.Filters.JwtAuthenticationFilter;
import com.example.Annuaire.Filters.JwtAuthorizationFilter;
import com.example.Annuaire.Service.RefreshTokenService;
import com.example.Annuaire.Service.UserDetailsServiceImpl;
import com.example.Annuaire.Utils.JwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtUtility jwtUtility,
            RefreshTokenService refreshTokenService) {
        this.userDetailsService = userDetailsService;
        this.jwtUtility = jwtUtility;
        this.refreshTokenService = refreshTokenService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests(requests -> requests.requestMatchers("/api/auth/login")
                        .permitAll().requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/refreshtoken").permitAll()
                        .requestMatchers("/api/auth/logout").permitAll()
                        .requestMatchers("/api/password/forgot").permitAll()
                        .requestMatchers("/api/password/reset").permitAll()
                        .requestMatchers("/api/password/validate-token/**").permitAll()
                        .requestMatchers("/api/changes/**").hasRole("admin")
                        .requestMatchers("/api/sync/**").hasRole("admin")
                        .requestMatchers("/api/user/**").hasRole("VISITOR")
                        .requestMatchers("/api/users/**").hasAuthority("super-admin")
                        .requestMatchers("/api/ps/**").permitAll().and()
                        .addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtUtility,
                                refreshTokenService))
                        .addFilterBefore(new JwtAuthorizationFilter(jwtUtility, userDetailsService),
                                UsernamePasswordAuthenticationFilter.class));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(
                Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration
                .setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Auth-Token"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(authenticationProvider()));
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
