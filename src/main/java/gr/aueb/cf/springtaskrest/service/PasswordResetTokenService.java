package gr.aueb.cf.springtaskrest.service;

import gr.aueb.cf.springtaskrest.core.exceptions.*;
import gr.aueb.cf.springtaskrest.model.PasswordResetToken;
import gr.aueb.cf.springtaskrest.model.User;
import gr.aueb.cf.springtaskrest.repository.PasswordResetTokenRepository;
import gr.aueb.cf.springtaskrest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private static final int TOKEN_EXPIRATION_MINUTES = 30;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;


    @Transactional
    public PasswordResetToken generateTokenForUser(String username) throws AppServerException {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AppObjectNotFoundException("User", "User with username " + username + " not found"));

            // Check for existing valid token
            Optional<PasswordResetToken> existingToken = passwordResetTokenRepository.findByUser(user);
            if (existingToken.isPresent() && existingToken.get().isTokenValid()) {
                return existingToken.get();
            }

            // Delete any expired token
            existingToken.ifPresent(passwordResetTokenRepository::delete);

            // Create new token
            PasswordResetToken newToken = new PasswordResetToken();
            newToken.setUser(user);
            newToken.setExpiryDate(LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES));

            return passwordResetTokenRepository.save(newToken);
        } catch (Exception e) {
            throw new AppServerException("Failed to generate password reset token", e.getMessage());
        }
    }


    @Transactional(readOnly = true)
    public boolean validateToken(String username, String token) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isEmpty()) {
                return false;
            }
            Optional<PasswordResetToken> tokenOptional = passwordResetTokenRepository.findByToken(token);
            if (tokenOptional.isEmpty()) {
                return false;
            }

            PasswordResetToken resetToken = tokenOptional.get();
            boolean isValid = resetToken.getUser().equals(userOptional.get()) &&
                    resetToken.isTokenValid();

            return isValid;
        } catch (Exception e) {
            return false;
        }
    }


    @Transactional(readOnly = true)
    public User getUserForValidToken(String token) throws AppObjectNotFoundException {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new AppObjectNotFoundException("Token", "Password reset token not found"));
        if (!resetToken.isTokenValid()) {
            throw new AppObjectNotFoundException("Token", "Password reset token has expired");
        }
        return resetToken.getUser();
    }
}