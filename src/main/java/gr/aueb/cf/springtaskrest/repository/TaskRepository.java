package gr.aueb.cf.springtaskrest.repository;

import gr.aueb.cf.springtaskrest.model.Task;
import gr.aueb.cf.springtaskrest.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    Optional<Task> findByUuid(String uuid);
    Optional<Task> findByTitleAndUser(String title, User user);
    Optional<Task> findByUuidAndUser(String uuid, User user);
    Page<Task> findByUserUuid(String uuid, Pageable pageable);
    Optional<Task> findByTitleAndUserUuid(String title, String userUuid);
    void deleteByUserUuid(String userUuid);
}
