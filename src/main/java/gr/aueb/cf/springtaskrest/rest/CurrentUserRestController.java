package gr.aueb.cf.springtaskrest.rest;

import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.springtaskrest.core.exceptions.ValidationException;
import gr.aueb.cf.springtaskrest.core.filters.TaskFilters;
import gr.aueb.cf.springtaskrest.dto.*;
import gr.aueb.cf.springtaskrest.model.User;
import gr.aueb.cf.springtaskrest.service.TaskService;
import gr.aueb.cf.springtaskrest.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class CurrentUserRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentUserRestController.class);
    private final UserService userService;
    private final TaskService taskService;


    @GetMapping
    public ResponseEntity<UserReadOnlyDTO> getMe(
            @AuthenticationPrincipal User user
    ) throws AppObjectNotFoundException {
        try {
            UserReadOnlyDTO readOnlyDTO = userService.findByUuid(user.getUuid());
            return new ResponseEntity<>(readOnlyDTO, HttpStatus.OK);
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Getting user failed. {}", e.getMessage(), e);
            throw e;
        }
    }

    @PatchMapping
    public ResponseEntity<UserReadOnlyDTO> updateMe(
            @Valid @RequestBody UserUpdateDTO dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal User user
    ) throws ValidationException, AppObjectNotFoundException, AppObjectAlreadyExistsException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        try {
            UserReadOnlyDTO updatedUser = userService.updateUser(user.getUuid(), dto);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (AppObjectNotFoundException | AppObjectAlreadyExistsException e) {
            LOGGER.error("Updating user failed. {}", e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMe(
            @AuthenticationPrincipal User user
    ) throws AppObjectNotFoundException {
        try {
            userService.reverseUserStatusActivity(user.getUuid());
            return ResponseEntity.noContent().build();
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Deleting user failed. {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/tasks")
    public ResponseEntity<Paginated<TaskReadOnlyDTO>> getCurrentUserTasks(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        TaskFilters filters = TaskFilters.builder().userUuid(user.getUuid()).build();
        LOGGER.error("Getting current user tasks. {}", filters);
        filters.setPage(page);
        filters.setSize(size);
        return new ResponseEntity<>(taskService.getFilteredPaginatedTasks(filters), HttpStatus.OK);
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskReadOnlyDTO> createTask(
            @Valid @RequestBody TaskInsertDTO taskInsertDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal User user
            ) throws ValidationException, AppObjectNotFoundException, AppObjectAlreadyExistsException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        try {
            TaskReadOnlyDTO readOnlyDTO = taskService.createTask(user.getUuid(), taskInsertDTO);
            LOGGER.info("Created new task: {}", readOnlyDTO);
            return new ResponseEntity<>(readOnlyDTO, HttpStatus.CREATED);
        } catch (AppObjectNotFoundException | AppObjectAlreadyExistsException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/tasks")
    public ResponseEntity<Void> deleteAllCurrentUserTasks(
            @AuthenticationPrincipal User user
    ) throws AppObjectNotFoundException {
        try {
            taskService.deleteAllUserTasks(user.getUuid());
            LOGGER.info("Deleted all current user tasks: {}", user.getUuid());
            return ResponseEntity.noContent().build();
        } catch (AppObjectNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/tasks/{taskUuid}")
    public ResponseEntity<TaskReadOnlyDTO> getCurrentUserTaskByUuid(
            @AuthenticationPrincipal User user,
            @PathVariable String taskUuid
    ) throws AppObjectNotFoundException {
        try {
            TaskReadOnlyDTO readOnlyDTO = taskService.findTaskByUserUuidAndTaskUuid(user.getUuid(), taskUuid);
            LOGGER.info("Retrieved task: {}", readOnlyDTO);
            return new ResponseEntity<>(readOnlyDTO, HttpStatus.OK);
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Could not find task with uuid: {}", taskUuid, e);
            throw e;
        }
    }

    @PatchMapping("/tasks/{taskUuid}")
    public ResponseEntity<TaskReadOnlyDTO> updateTask(
            @PathVariable("taskUuid") String taskUuid,
            @Valid @RequestBody TaskUpdateDTO updateDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal User user
    ) throws ValidationException, AppObjectNotFoundException, AppObjectAlreadyExistsException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        try {
            TaskReadOnlyDTO readOnlyDTO = taskService.updateTask(user.getUuid(), taskUuid, updateDTO);
            LOGGER.info("Updated task: {}", readOnlyDTO);
            return new ResponseEntity<>(readOnlyDTO, HttpStatus.OK);
        } catch (AppObjectNotFoundException | AppObjectAlreadyExistsException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/tasks/{taskUuid}")
    public ResponseEntity<Void> deleteUserTaskByUuid(
            @PathVariable("taskUuid") String taskUuid,
            @AuthenticationPrincipal User user
    ) throws AppObjectNotFoundException {
        try {
            taskService.deleteTaskByUuidAndUserUuid(user.getUuid(), taskUuid);
            LOGGER.info("Deleted task: {}", taskUuid);
            return ResponseEntity.noContent().build();
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Could not find task with uuid: {}", taskUuid, e);
            throw e;
        }
    }
}
