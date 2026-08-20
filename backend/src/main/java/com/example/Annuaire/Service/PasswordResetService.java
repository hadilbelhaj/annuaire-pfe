package com.example.Annuaire.Service;

import com.example.Annuaire.Models.PasswordResetToken;
import com.example.Annuaire.Models.User;
import com.example.Annuaire.Repository.PasswordResetTokenRepository;
import com.example.Annuaire.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public boolean initiatePasswordReset(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {

            return false;
        }

        tokenRepository.deleteByEmail(email);

        PasswordResetToken token = new PasswordResetToken();
        token.setEmail(email);
        tokenRepository.save(token);

        emailService.sendPasswordResetEmail(email, token.getToken());

        return true;
    }

    @Transactional
    public boolean completePasswordReset(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);

        if (tokenOptional.isEmpty() || tokenOptional.get().isExpired()) {
            return false;
        }

        PasswordResetToken resetToken = tokenOptional.get();
        String email = resetToken.getEmail();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);

        return true;
    }
}