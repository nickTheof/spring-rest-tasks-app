package gr.aueb.cf.springtaskrest.mapper;

import gr.aueb.cf.springtaskrest.core.enums.Role;
import gr.aueb.cf.springtaskrest.core.enums.TaskStatus;
import gr.aueb.cf.springtaskrest.core.filters.TaskFilters;
import gr.aueb.cf.springtaskrest.core.filters.UserFilters;
import gr.aueb.cf.springtaskrest.dto.*;
import gr.aueb.cf.springtaskrest.model.Task;
import gr.aueb.cf.springtaskrest.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Mapper {
    private final PasswordEncoder passwordEncoder;

    public User mapToUser(UserInsertDTO dto) {
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(Role.valueOf(dto.role()));
        user.setIsActive(dto.isActive());
        return user;
    }

    public User mapToUser(UserRegisterDTO dto) {
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(Role.USER);
        user.setIsActive(true);
        return user;
    }

    public User mapToUser(UserUpdateDTO dto, User user) {
        if (dto.username() != null) {
            user.setUsername(dto.username());
        }
        if (dto.password() != null) {
            user.setPassword(passwordEncoder.encode(dto.password()));
            user.setLastPasswordChange(Instant.now());
        }
        if (dto.role() != null) {
            user.setRole(Role.valueOf(dto.role()));
        }
        if (dto.isActive() != null) {
            user.setIsActive(dto.isActive());
        }
        return user;
    }

    public UserReadOnlyDTO mapToUserReadOnly(User user) {
        return new UserReadOnlyDTO(user.getId(), user.getUuid(), user.getUsername(), user.getIsActive(), user.getRole().name());
    }

    public Task mapToTask(TaskInsertDTO dto) {
        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setStatus(TaskStatus.valueOf(dto.status()));
        return task;
    }

    public Task mapToTask(TaskUpdateDTO dto, Task task) {
        if (dto.title() != null) {
            task.setTitle(dto.title());
        }
        if (dto.description() != null) {
            task.setDescription(dto.description());
        }
        if (dto.status() != null) {
            task.setStatus(TaskStatus.valueOf(dto.status()));
        }
        return task;
    }

    public TaskReadOnlyDTO mapToTaskReadOnly(Task task) {
        UserReadOnlyDTO userReadOnlyDTO = mapToUserReadOnly(task.getUser());
        return new TaskReadOnlyDTO(task.getId(), task.getUuid(), task.getTitle(), task.getDescription(), task.getStatus().name(), userReadOnlyDTO);
    }

    public UserFilters mapToUserFilters(UserFiltersDTO dto) {
        UserFilters userFilters = new UserFilters();
        if (dto.page() != null) {
            userFilters.setPage(dto.page());
        }
        if (dto.size() != null) {
            userFilters.setSize(dto.size());
        }
        if (dto.sortBy() != null) {
            userFilters.setSortBy(dto.sortBy());
        }
        if (dto.orderBy() != null) {
            userFilters.setOrderBy(Sort.Direction.valueOf(dto.orderBy()));
        }
        if (dto.username() != null) {
            userFilters.setUsername(dto.username());
        }
        if (dto.role() != null) {
            userFilters.setRole(Role.valueOf(dto.role()));
        }
        if (dto.active() != null) {
            userFilters.setActive(dto.active());
        }
        if (dto.uuid() != null) {
            userFilters.setUuid(dto.uuid());
        }
        return userFilters;
    }

    public TaskFilters mapToTaskFilters(TaskFiltersDTO dto) {
        TaskFilters taskFilters = new TaskFilters();
        if (dto.page() != null) {
            taskFilters.setPage(dto.page());
        }
        if (dto.size() != null) {
            taskFilters.setSize(dto.size());
        }
        if (dto.sortBy() != null) {
            taskFilters.setSortBy(dto.sortBy());
        }
        if (dto.orderBy() != null) {
            taskFilters.setOrderBy(Sort.Direction.valueOf(dto.orderBy()));
        }
        if (dto.uuid() != null) {
            taskFilters.setUuid(dto.uuid());
        }
        if (dto.taskStatus() != null) {
            taskFilters.setStatus(dto.taskStatus().stream().map(TaskStatus::valueOf).collect(Collectors.toList()));
        }

        if (dto.title() != null) {
            taskFilters.setTitle(dto.title());
        }
        if (dto.userIsActive() != null) {
            taskFilters.setUserIsActive(dto.userIsActive());
        }
        if (dto.uuid() != null) {
            taskFilters.setUuid(dto.uuid());
        }
        if (dto.userUuid() != null) {
            taskFilters.setUserUuid(dto.userUuid());
        }
        return taskFilters;
    }
}
