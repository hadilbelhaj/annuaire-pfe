package com.example.Annuaire.Service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Annuaire.Exceptions.TokenRefreshException;
import com.example.Annuaire.Models.RefreshToken;
import com.example.Annuaire.Repository.RefreshTokenRepository;
import com.example.Annuaire.Utils.JwtUtility;

@Service
public class RefreshTokenService {
    @Value("${jwt.refresh-expiration:604800000}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtUtility jwtUtility;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(String email) {
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByEmail(email);
        if (existingToken.isPresent()) {
            refreshTokenRepository.deleteByEmail(email);
        }

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setEmail(email);
        refreshToken.setToken(jwtUtility.generateRefreshToken(email));
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(),
                    "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public void deleteByEmail(String email) {
        refreshTokenRepository.deleteByEmail(email);
    }
}
