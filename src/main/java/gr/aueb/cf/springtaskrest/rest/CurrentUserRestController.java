package gr.aueb.cf.springtaskrest.rest;

import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.springtaskrest.core.exceptions.ValidationException;
import gr.aueb.cf.springtaskrest.core.filters.TaskFilters;
import gr.aueb.cf.springtaskrest.dto.*;
import gr.aueb.cf.springtaskrest.model.User;
import gr.aueb.cf.springtaskrest.service.TaskService;
import gr.aueb.cf.springtaskrest.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "CurrentUser")
public class CurrentUserRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentUserRestController.class);
    private final UserService userService;
    private final TaskService taskService;


    @Operation(
            summary = "Get current authenticated user",
            description = "Retrieve information of the current authenticated user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieve information of the current authenticated user",
                            content = @Content(
                                    schema = @Schema(implementation = UserReadOnlyDTO.class)
                            )
                    )
            }
    )
    @Tag(name = "Users")
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

    @Operation(
            summary = "Update current authenticated user",
            description = "Update information (such as name, email, etc.) of the current authenticated user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserUpdateDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User updated successfully",
                            content = @Content(schema = @Schema(implementation = UserReadOnlyDTO.class))
                    )
            }
    )
    @Tag(name = "Users")
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


    @Operation(
            summary = "Deactivate current authenticated user",
            description = "Soft deletes (deactivates) the current authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deactivated"),
            }
    )
    @Tag(name = "Users")
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

    @Operation(
            summary = "Get current user's tasks (paginated)",
            description = "Get a paginated list of tasks belonging to the current authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Paginated list of tasks for current user"
                    )
            }
    )
    @Tag(name = "Tasks")
    @GetMapping("/tasks")
    public ResponseEntity<Paginated<TaskReadOnlyDTO>> getCurrentUserTasks(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        TaskFiltersDTO filters = new TaskFiltersDTO(page, size, user.getUuid());
        LOGGER.error("Getting current user tasks. {}", filters);
        return new ResponseEntity<>(taskService.getFilteredPaginatedTasks(filters), HttpStatus.OK);
    }



    @Operation(
            summary = "Create a new task for current user",
            description = "Creates a new task assigned to the current authenticated user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = TaskInsertDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Task created successfully",
                            content = @Content(schema = @Schema(implementation = TaskReadOnlyDTO.class))
                    )
            }
    )
    @Tag(name = "Tasks")
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

    @Operation(
            summary = "Delete all tasks of current user",
            description = "Deletes all tasks associated with the current authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "All user tasks deleted"),
            }
    )
    @Tag(name = "Tasks")
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

    @Operation(
            summary = "Get a specific task of current user by UUID",
            description = "Retrieves a specific task belonging to the current authenticated user by its UUID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Task found",
                            content = @Content(schema = @Schema(implementation = TaskReadOnlyDTO.class))
                    )
            }
    )
    @Tag(name = "Tasks")
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

    @Operation(
            summary = "Update a task of current user by UUID",
            description = "Updates a specific task of the current authenticated user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = TaskUpdateDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Task updated",
                            content = @Content(schema = @Schema(implementation = TaskReadOnlyDTO.class))
                    )
            }
    )
    @Tag(name = "Tasks")
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

    @Operation(
            summary = "Delete a specific task of current user by UUID",
            description = "Deletes a task belonging to the current authenticated user by its UUID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Task deleted"),
            }
    )
    @Tag(name = "Tasks")
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
