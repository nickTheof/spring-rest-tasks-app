package gr.aueb.cf.springtaskrest.rest;

import gr.aueb.cf.springtaskrest.authentication.AuthenticationService;
import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectNotAuthorizedException;
import gr.aueb.cf.springtaskrest.core.exceptions.ValidationException;
import gr.aueb.cf.springtaskrest.dto.*;
import gr.aueb.cf.springtaskrest.service.IUserService;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthRestController {
    private final IUserService userService;
    private final AuthenticationService authenticationService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthRestController.class);

    @Operation(
            summary = "Register a new user with role USER",
            description = "Creates a new user account. Returns the created user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UserRegisterDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User created",
                            content = @Content(
                                    schema = @Schema(implementation = UserReadOnlyDTO.class)
                            )
                    )
            },
            security = {}
    )
    @PostMapping("/register")
    public ResponseEntity<UserReadOnlyDTO> register(
            @Valid @RequestBody UserRegisterDTO dto,
            BindingResult bindingResult) throws ValidationException, AppObjectAlreadyExistsException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        try {
            UserReadOnlyDTO userReadOnlyDTO = userService.registerUser(dto);
            return new ResponseEntity<>(userReadOnlyDTO, HttpStatus.CREATED);
        } catch ( AppObjectAlreadyExistsException e) {
            LOGGER.error("User registration failed. {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(
            summary = "Authenticate a user",
            description = "Authenticate user and return authentication response (e.g., JWT).",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AuthenticationRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User authenticated",
                            content = @Content(
                                    schema = @Schema(implementation = AuthenticationResponseDTO.class)
                            )
                    )
            },
            security = {}
    )
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(
            @RequestBody AuthenticationRequestDTO authenticationRequestDTO
            ) throws AppObjectNotAuthorizedException {
        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.authenticate(authenticationRequestDTO);
        LOGGER.info("User authenticated: {}", authenticationResponseDTO);
        return new ResponseEntity<>(authenticationResponseDTO, HttpStatus.OK);
    }
}
