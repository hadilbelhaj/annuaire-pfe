package com.example.Annuaire.Controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Annuaire.Exceptions.TokenRefreshException;
import com.example.Annuaire.Models.LoginRequest;
import com.example.Annuaire.Models.RefreshToken;
import com.example.Annuaire.Models.TokenRefreshRequest;
import com.example.Annuaire.Models.TokenRefreshResponse;
import com.example.Annuaire.Models.User;
import com.example.Annuaire.Repository.UserRepository;
import com.example.Annuaire.Service.RefreshTokenService;
import com.example.Annuaire.Service.UserService;
import com.example.Annuaire.Service.Localisation.GeocodingService;
import com.example.Annuaire.Utils.JwtUtility;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private final UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtility jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public List<User> getAll() {
        List<User> users = userRepository.findAll();
        return users;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            user.setRole("admin");
        } else if ("SUPER_ADMIN".equalsIgnoreCase(user.getRole())) {
            user.setRole("super-admin");
        } else {
            user.setRole("VISITOR");
        }
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        String token = jwtUtil.generateToken(userDetails.getUsername(), roles);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", userDetails.getUsername());
        response.put("roles", roles);

        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getEmail)
                .map(email -> {

                    Optional<User> userOptional = userRepository.findByEmail(email);
                    if (!userOptional.isPresent()) {
                        throw new TokenRefreshException(requestRefreshToken, "User not found with email: " + email);
                    }

                    User user = userOptional.get();
                    List<String> roles = Collections.singletonList(user.getRole());

                    String accessToken = jwtUtil.generateToken(email, roles);

                    return ResponseEntity.ok(new TokenRefreshResponse(accessToken, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token not found!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email != null) {
            refreshTokenService.deleteByEmail(email);
            return ResponseEntity.ok("Log out successful");
        }

        return ResponseEntity.badRequest().body("Email is required");
    }

    @GetMapping("/roles/{email}")
    public ResponseEntity<?> getUserRoles(@PathVariable String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            String roles = user.get().getRole();
            return ResponseEntity.ok(roles);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<User> updateProfile(
            @PathVariable Long id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String address) {

        User updatedUser = userService.updateProfile(id, firstName, lastName, address);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/geoCode/{id}")
    public ResponseEntity<?> geocodeUser(@PathVariable Long id) {
        boolean success = userService.updateUserCoordinates(id);
        return success ? ResponseEntity.ok("User geocoded.")
                : ResponseEntity.status(404).body("User not found or failed to geocode.");
    }

    @PutMapping("/geoCode/all")
    public ResponseEntity<?> geocodeAllUsers() {
        userService.updateAllUsersCoordinates();
        return ResponseEntity.ok("All users processed.");
    }

    @GetMapping("/geoCode")
    public ResponseEntity<?> geocodeAddress(@RequestParam String adress) {
        if (adress != null) {
            double[] coords = geocodingService.geocodeAddress(adress);
            if (coords != null) {
                return ResponseEntity.ok(coords);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Coordinates not found");
            }
        }
        return ResponseEntity.badRequest().body("Address is required");
    }

}
