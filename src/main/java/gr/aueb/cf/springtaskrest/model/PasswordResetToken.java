package gr.aueb.cf.springtaskrest.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reset_tokens_verification")
@EntityListeners(AuditingEntityListener.class)
public class PasswordResetToken {

    private static final int DEFAULT_EXPIRATION_MINUTES = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @PrePersist
    protected void onCreate() {
        if (expiryDate == null) {
            expiryDate = calculateExpiryDate(DEFAULT_EXPIRATION_MINUTES);
        }
        if (token == null) {
            token = generateRandomToken();
        }
    }

    public boolean isTokenValid() {
        return expiryDate != null && expiryDate.isAfter(LocalDateTime.now());
    }

    public void updateToken() {
        this.token = generateRandomToken();
        this.expiryDate = calculateExpiryDate(DEFAULT_EXPIRATION_MINUTES);
    }

    private String generateRandomToken() {
        return UUID.randomUUID().toString();
    }

    private LocalDateTime calculateExpiryDate(int expiryTimeInMinutes) {
        return LocalDateTime.now().plusMinutes(expiryTimeInMinutes);
    }
}