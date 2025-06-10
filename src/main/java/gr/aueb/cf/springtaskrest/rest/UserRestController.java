package gr.aueb.cf.springtaskrest.rest;

import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.springtaskrest.core.exceptions.ValidationException;
import gr.aueb.cf.springtaskrest.dto.*;
import gr.aueb.cf.springtaskrest.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tags({
        @Tag(name = "Admin"),
        @Tag(name = "Users")
})
public class UserRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRestController.class);
    private final UserService userService;

    @Operation(
            summary = "Get all users (paginated)",
            description = "Returns a paginated list of all users. Only accessible by admin.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval of users"
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Page<UserReadOnlyDTO>> getAllUsersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<UserReadOnlyDTO> usersPage = userService.getUsersPaginated(page, size);
        return new ResponseEntity<>(usersPage, HttpStatus.OK);
    }

    @Operation(
            summary = "Insert a new user",
            description = "Admin creates a new user with selected role and status",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UserInsertDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User created successfully",
                            content = @Content(
                                    schema = @Schema(implementation = UserReadOnlyDTO.class)
                            )
                    )
            }
    )
    @PostMapping
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

    @Operation(
            summary = "Delete all users",
            description = "Admin deletes all users cascades to a delete to all tasks.",
            responses = {
                    @ApiResponse(
                            responseCode = "204", description = "Users successfully deleted"
                    )
            }
    )
    @DeleteMapping
    public ResponseEntity<Void> deleteAllUsers() {
        userService.deleteAllUsers();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Operation(
            summary = "Get filtered users (paginated)",
            description = "Returns a paginated list of users matching provided filters. Only accessible by admin.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = UserFiltersDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Filtered users retrieved successfully"
                    )
            }
    )
    @PostMapping("/filtered")
    public ResponseEntity<Paginated<UserReadOnlyDTO>> getFilteredUsersPaginated(
            @Nullable @RequestBody UserFiltersDTO filters
            ) {
        if (filters == null) filters = new UserFiltersDTO();
        return new ResponseEntity<>(userService.getUsersFilteredPaginated(filters), HttpStatus.OK);
    }


    @Operation(
            summary = "Get user by UUID",
            description = "Retrieves a user by their unique UUID. Only accessible by admin.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User found",
                            content = @Content(schema = @Schema(implementation = UserReadOnlyDTO.class))
                    ),
            }
    )
    @GetMapping("/{uuid}")
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

    @Operation(
            summary = "Update user by UUID",
            description = "Updates a user with the given UUID. Only accessible by admin.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserUpdateDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User updated successfully",
                            content = @Content(schema = @Schema(implementation = UserReadOnlyDTO.class))
                    ),
            }
    )
    @PatchMapping("/{uuid}")
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


    @Operation(
            summary = "Delete user by UUID",
            description = "Deletes a user by their unique UUID. Only accessible by admin.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "User deleted successfully"
                    ),
            }
    )
    @DeleteMapping("/{uuid}")
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

}
