package gr.aueb.cf.springtaskrest.repository;

import gr.aueb.cf.springtaskrest.model.PasswordResetToken;
import gr.aueb.cf.springtaskrest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);
}
