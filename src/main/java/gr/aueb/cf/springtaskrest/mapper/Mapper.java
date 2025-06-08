package gr.aueb.cf.springtaskrest.mapper;

import gr.aueb.cf.springtaskrest.dto.*;
import gr.aueb.cf.springtaskrest.model.Task;
import gr.aueb.cf.springtaskrest.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mapper {
    private final PasswordEncoder passwordEncoder;

    public User mapToUser(UserInsertDTO dto) {
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(dto.role());
        user.setIsActive(dto.isActive());
        return user;
    }

    public User mapToUser(UserUpdateDTO dto, User user) {
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(dto.role());
        user.setIsActive(dto.isActive());
        return user;
    }

    public UserReadOnlyDTO mapToUserReadOnly(User user) {
        return new UserReadOnlyDTO(user.getId(), user.getUuid(), user.getUsername(), user.getIsActive(), user.getRole().name());
    }

    public Task mapToTask(TaskInsertDTO dto) {
        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setStatus(dto.status());
        return task;
    }

    public Task mapToTask(TaskUpdateDTO dto, Task task) {
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setStatus(dto.status());
        return task;
    }

    public TaskReadOnlyDTO mapToTaskReadOnly(Task task) {
        UserReadOnlyDTO userReadOnlyDTO = mapToUserReadOnly(task.getUser());
        return new TaskReadOnlyDTO(task.getId(), task.getUuid(), task.getTitle(), task.getDescription(), task.getStatus().name(), userReadOnlyDTO);
    }
}
