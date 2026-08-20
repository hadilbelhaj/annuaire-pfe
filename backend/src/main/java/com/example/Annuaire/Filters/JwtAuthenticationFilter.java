package com.example.Annuaire.Filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.Annuaire.Models.LoginRequest;
import com.example.Annuaire.Models.RefreshToken;
import com.example.Annuaire.Service.RefreshTokenService;
import com.example.Annuaire.Utils.JwtUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtility jwtUtility;
    private final RefreshTokenService refreshTokenService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
            JwtUtility jwtUtility, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtility = jwtUtility;
        this.refreshTokenService = refreshTokenService;
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequest credentials =
                    new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    credentials.getEmail(), credentials.getPassword(), new ArrayList<>()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain, Authentication authResult) {
        UserDetails userDetails = (UserDetails) authResult.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        String token = jwtUtility.generateToken(userDetails.getUsername(), roles);

        // Create refresh token
        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(userDetails.getUsername());

        response.addHeader("Authorization", "Bearer " + token);
        response.addHeader("Content-Type", "application/json");

        // Create a response map with both tokens and user info
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("token", token);
        responseMap.put("refreshToken", refreshToken.getToken());
        responseMap.put("email", userDetails.getUsername());
        responseMap.put("roles", roles);

        try {
            // Write the full response as JSON
            new ObjectMapper().writeValue(response.getWriter(), responseMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
