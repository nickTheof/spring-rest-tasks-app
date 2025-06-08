package gr.aueb.cf.springtaskrest.repository;

import gr.aueb.cf.springtaskrest.core.enums.Role;
import gr.aueb.cf.springtaskrest.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> , JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUuid(String uuid);
    Page<User> findByRole(Role role, Pageable pageable);
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
}
