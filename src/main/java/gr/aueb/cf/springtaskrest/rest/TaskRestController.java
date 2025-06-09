package gr.aueb.cf.springtaskrest.rest;

import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.springtaskrest.core.exceptions.ValidationException;
import gr.aueb.cf.springtaskrest.core.filters.TaskFilters;
import gr.aueb.cf.springtaskrest.dto.Paginated;
import gr.aueb.cf.springtaskrest.dto.TaskInsertDTO;
import gr.aueb.cf.springtaskrest.dto.TaskReadOnlyDTO;
import gr.aueb.cf.springtaskrest.dto.TaskUpdateDTO;
import gr.aueb.cf.springtaskrest.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TaskRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRestController.class);
    private final TaskService taskService;

    @GetMapping("/tasks")
    public ResponseEntity<Paginated<TaskReadOnlyDTO>> getAllTasksPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        TaskFilters filters = TaskFilters.builder().build();
        filters.setPage(page);
        filters.setSize(size);
        var tasks = taskService.getFilteredPaginatedTasks(filters);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @DeleteMapping("/tasks")
    public ResponseEntity<Void> deleteAllTasks() {
        taskService.deleteAllTasks();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tasks/filtered")
    public ResponseEntity<Paginated<TaskReadOnlyDTO>> getFilteredTasksPaginated(
            @Nullable @RequestBody TaskFilters filters
            ) {
        if (filters == null) filters = TaskFilters.builder().build();
        var tasks = taskService.getFilteredPaginatedTasks(filters);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @GetMapping("/tasks/{uuid}")
    public ResponseEntity<TaskReadOnlyDTO> getTaskByUuid(
        @PathVariable("uuid") String uuid
    ) throws AppObjectNotFoundException {
        try {
            TaskReadOnlyDTO readOnlyDTO = taskService.findTaskByUuid(uuid);
            return new ResponseEntity<>(readOnlyDTO, HttpStatus.OK);
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Could not find task with uuid: {}", uuid, e);
            throw e;
        }
    }


    @DeleteMapping("/tasks/{uuid}")
    public ResponseEntity<Void> deleteTaskByUuid(
            @PathVariable("uuid") String uuid
    ) throws AppObjectNotFoundException {
        try {
            taskService.deleteTaskByUuid(uuid);
            return ResponseEntity.noContent().build();
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Could not find task with uuid: {}", uuid, e);
            throw e;
        }
    }

    @GetMapping("/users/{userUuid}/tasks")
    public ResponseEntity<Paginated<TaskReadOnlyDTO>> getAllUserTasksPaginated(
            @PathVariable("userUuid") String userUuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        TaskFilters filters = TaskFilters.builder().userUuid(userUuid).build();
        filters.setPage(page);
        filters.setSize(size);
        return new ResponseEntity<>(taskService.getFilteredPaginatedTasks(filters), HttpStatus.OK);
    }

    @PostMapping("/users/{userUuid}/tasks")
    public ResponseEntity<TaskReadOnlyDTO> createTask(
            @PathVariable String userUuid,
            @Valid @RequestBody TaskInsertDTO taskInsertDTO,
            BindingResult bindingResult) throws ValidationException, AppObjectNotFoundException, AppObjectAlreadyExistsException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        try {
            TaskReadOnlyDTO readOnlyDTO = taskService.createTask(userUuid, taskInsertDTO);
            LOGGER.info("Created new task: {}", readOnlyDTO);
            return new ResponseEntity<>(readOnlyDTO, HttpStatus.CREATED);
        } catch (AppObjectNotFoundException | AppObjectAlreadyExistsException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/users/{userUuid}/tasks/{taskUuid}")
    public ResponseEntity<TaskReadOnlyDTO> getUserTaskByUuid(
            @PathVariable("userUuid") String userUuid,
            @PathVariable("taskUuid") String taskUuid
    ) throws AppObjectNotFoundException {
        try {
            TaskReadOnlyDTO readOnlyDTO = taskService.findTaskByUserUuidAndTaskUuid(userUuid, taskUuid);
            LOGGER.info("Retrieved task: {}", readOnlyDTO);
            return new ResponseEntity<>(readOnlyDTO, HttpStatus.OK);
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Could not find task with uuid: {}", taskUuid, e);
            throw e;
        }
    }

    @PatchMapping("/users/{userUuid}/tasks/{taskUuid}")
    public ResponseEntity<TaskReadOnlyDTO> updateTask(
            @PathVariable("userUuid") String userUuid,
            @PathVariable("taskUuid") String taskUuid,
            @Valid @RequestBody TaskUpdateDTO updateDTO,
            BindingResult bindingResult
    ) throws ValidationException, AppObjectNotFoundException, AppObjectAlreadyExistsException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        try {
            TaskReadOnlyDTO readOnlyDTO = taskService.updateTask(userUuid, taskUuid, updateDTO);
            LOGGER.info("Updated task: {}", readOnlyDTO);
            return new ResponseEntity<>(readOnlyDTO, HttpStatus.OK);
        } catch (AppObjectNotFoundException | AppObjectAlreadyExistsException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/users/{userUuid}/tasks/{taskUuid}")
    public ResponseEntity<Void> deleteUserTaskByUuid(
            @PathVariable("userUuid") String userUuid,
            @PathVariable("taskUuid") String taskUuid
    ) throws AppObjectNotFoundException {
        try {
            taskService.deleteTaskByUuidAndUserUuid(userUuid, taskUuid);
            LOGGER.info("Deleted task: {}", taskUuid);
            return ResponseEntity.noContent().build();
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Could not find task with uuid: {}", taskUuid, e);
            throw e;
        }
    }
}
