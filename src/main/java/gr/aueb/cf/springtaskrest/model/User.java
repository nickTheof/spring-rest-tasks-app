package gr.aueb.cf.springtaskrest.model;

import gr.aueb.cf.springtaskrest.core.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User extends AbstractEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, updatable = false)
    private String uuid;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "last_password_change", nullable = false)
    private Instant lastPasswordChange;

    @ColumnDefault("true")
    @Column(nullable = false, name = "is_active")
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private PasswordResetToken passwordResetToken;

    @Getter(AccessLevel.PROTECTED)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasks = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return lastPasswordChange.isAfter(Instant.now().minus(90, ChronoUnit.DAYS));
    }

    @Override
    public boolean isEnabled() {
        return this.getIsActive();
    }

    @PrePersist
    protected void onPersist() {
        if (uuid == null) uuid = UUID.randomUUID().toString();
        if (lastPasswordChange == null) lastPasswordChange = Instant.now();
    }

    public Set<Task> getAllTasks() {
        return Collections.unmodifiableSet(tasks);
    }

    public void addTask(Task task) {
        if (tasks == null) tasks = new HashSet<>();
        if (task == null) return;
        tasks.add(task);
        task.setUser(this);
    }

    public void removeTask(Task task) {
        if (tasks == null || task == null || !tasks.contains(task)) return;
        tasks.remove(task);
        task.setUser(null);
    }

    // Password reset methods
    public void createPasswordResetToken() {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(this);
        this.passwordResetToken = resetToken;
    }

    public void clearPasswordResetToken() {
        if (this.passwordResetToken != null) {
            this.passwordResetToken.setUser(null);
            this.passwordResetToken = null;
        }
    }

    public boolean hasValidPasswordResetToken() {
        return this.passwordResetToken != null && this.passwordResetToken.isTokenValid();
    }
}
