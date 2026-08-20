package com.example.Annuaire.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Annuaire.Exceptions.ResourceNotFoundException;
import com.example.Annuaire.Models.User;
import com.example.Annuaire.Repository.UserRepository;
import com.example.Annuaire.Service.Localisation.GeocodingService;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    private GeocodingService geocodingService;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            logger.warn("Authentication is null in getCurrentUser");
            return null;
        }

        String username = authentication.getName();
        logger.info("Attempting to find user with email: {}", username);

        try {
            return userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        } catch (UsernameNotFoundException e) {
            logger.error("User not found for email: {}", username, e);
            return null;
        }
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateProfile(Long id, String firstName, String lastName, String address) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setFirstName(firstName);
        user.setLastName(lastName);
        boolean addressChanged = !address.equals(user.getAddress());
        user.setAddress(address);
        if (addressChanged) {
            double[] coords = geocodingService.geocodeAddress(address);
            if (coords != null) {
                user.setLatitude(coords[0]);
                user.setLongitude(coords[1]);
                logger.info("Updated coordinates for user ID {}: lat={}, long={}", id, coords[0], coords[1]);
            } else {
                logger.warn("Failed to geocode address for user ID {}: {}", id, address);
            }
        }

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean updateUserCoordinates(Long id) {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            User user = optional.get();
            double[] coords = geocodingService.geocodeAddress(user.getAddress());
            if (coords != null) {
                user.setLatitude(coords[0]);
                user.setLongitude(coords[1]);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public void updateAllUsersCoordinates() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getLatitude() == null || user.getLongitude() == null) {
                double[] coords = geocodingService.geocodeAddress(user.getAddress());
                if (coords != null) {
                    user.setLatitude(coords[0]);
                    user.setLongitude(coords[1]);
                    userRepository.save(user);
                }
            }
        }
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setEmail(userDetails.getEmail());
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setAddress(userDetails.getAddress());
        user.setRole(userDetails.getRole());

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        if (userDetails.getLatitude() != null) {
            user.setLatitude(userDetails.getLatitude());
        }

        if (userDetails.getLongitude() != null) {
            user.setLongitude(userDetails.getLongitude());
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findByIdAndDeleted(id, 0)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setDeleted(1);
        userRepository.save(user);
    }

    public User restoreUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setDeleted(0);
        return userRepository.save(user);
    }
    public List<User> getAllActiveUsers() {
        return userRepository.findByDeleted(0);
    }

}
