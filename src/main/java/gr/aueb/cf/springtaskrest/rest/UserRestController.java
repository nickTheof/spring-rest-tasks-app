package gr.aueb.cf.springtaskrest.rest;

import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.springtaskrest.core.exceptions.ValidationException;
import gr.aueb.cf.springtaskrest.core.filters.UserFilters;
import gr.aueb.cf.springtaskrest.dto.Paginated;
import gr.aueb.cf.springtaskrest.dto.UserInsertDTO;
import gr.aueb.cf.springtaskrest.dto.UserReadOnlyDTO;
import gr.aueb.cf.springtaskrest.dto.UserUpdateDTO;
import gr.aueb.cf.springtaskrest.model.User;
import gr.aueb.cf.springtaskrest.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRestController.class);
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<Page<UserReadOnlyDTO>> getAllUsersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<UserReadOnlyDTO> usersPage = userService.getUsersPaginated(page, size);
        return new ResponseEntity<>(usersPage, HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<UserReadOnlyDTO> insertUser(
            @Valid @RequestBody UserInsertDTO dto,
            BindingResult bindingResult) throws ValidationException, AppObjectAlreadyExistsException {
        if (bindingResult.hasErrors()) {
                throw new ValidationException(bindingResult);
        }
        try {
            UserReadOnlyDTO readOnlyDTO = userService.saveUser(dto);
            return new ResponseEntity<>(readOnlyDTO, HttpStatus.CREATED);
        } catch (AppObjectAlreadyExistsException e) {
            LOGGER.error("Inserting user failed. {}", e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/users")
    public ResponseEntity<Void> deleteAllUsers() {
        userService.deleteAllUsers();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/users/filtered")
    public ResponseEntity<Paginated<UserReadOnlyDTO>> getFilteredUsersPaginated(
            @Nullable @RequestBody UserFilters filters
            ) {
        if (filters == null) filters = UserFilters.builder().build();
        return new ResponseEntity<>(userService.getUsersFilteredPaginated(filters), HttpStatus.OK);
    }

    @GetMapping("/users/{uuid}")
    public ResponseEntity<UserReadOnlyDTO> getUserByUuid(
            @PathVariable("uuid") String uuid
    ) throws AppObjectNotFoundException {
        try {
            UserReadOnlyDTO readOnlyDTO = userService.findByUuid(uuid);
            return new ResponseEntity<>(readOnlyDTO, HttpStatus.OK);
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Getting user failed. {}", e.getMessage(), e);
            throw e;
        }
    }

    @PatchMapping("/users/{uuid}")
    public ResponseEntity<UserReadOnlyDTO> updateUser(
            @PathVariable("uuid") String uuid,
            @Valid @RequestBody UserUpdateDTO dto,
            BindingResult bindingResult
            ) throws ValidationException, AppObjectNotFoundException, AppObjectAlreadyExistsException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        try {
            UserReadOnlyDTO readOnlyDTO = userService.updateUser(uuid, dto);
            return new ResponseEntity<>(readOnlyDTO, HttpStatus.OK);
        } catch (AppObjectNotFoundException | AppObjectAlreadyExistsException e) {
            LOGGER.error("Updating user failed. {}", e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/users/{uuid}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable("uuid") String uuid
    ) throws  AppObjectNotFoundException {
        try {
            userService.deleteUser(uuid);
            return ResponseEntity.noContent().build();
        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Deleting user failed. {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/users/me")
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

    @PatchMapping("/users/me")
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

    @DeleteMapping("/users/me")
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
}
