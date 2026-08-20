package com.example.Annuaire.Controllers;

import com.example.Annuaire.Models.PasswordResetRequest;
import com.example.Annuaire.Models.PasswordResetCompleteRequest;
import com.example.Annuaire.Service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@CrossOrigin(origins = "http://localhost:4200")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody PasswordResetRequest request) {
        boolean result = passwordResetService.initiatePasswordReset(request.getEmail());

        // Always return OK even if email doesn't exist (security best practice)
        return ResponseEntity.ok().body("If your email exists in our system, you will receive a password reset link");
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetCompleteRequest request) {
        boolean result = passwordResetService.completePasswordReset(
                request.getToken(), request.getNewPassword());

        if (result) {
            return ResponseEntity.ok().body("Password has been reset successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }
    }

    @GetMapping("/validate-token/{token}")
    public ResponseEntity<?> validateToken(@PathVariable String token) {
        return ResponseEntity.ok().build();
    }
}