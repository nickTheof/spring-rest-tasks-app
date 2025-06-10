package gr.aueb.cf.springtaskrest.rest;

import gr.aueb.cf.springtaskrest.authentication.AuthenticationService;
import gr.aueb.cf.springtaskrest.core.exceptions.*;
import gr.aueb.cf.springtaskrest.dto.*;
import gr.aueb.cf.springtaskrest.model.PasswordResetToken;
import gr.aueb.cf.springtaskrest.model.User;
import gr.aueb.cf.springtaskrest.service.EmailService;
import gr.aueb.cf.springtaskrest.service.IUserService;
import gr.aueb.cf.springtaskrest.service.PasswordResetTokenService;
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
import org.springframework.mail.MailException;
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
    private final EmailService emailService;
    private final PasswordResetTokenService passwordResetTokenService;
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


    @PostMapping("/forget-password")
    public ResponseEntity<ApiResponseDTO<Void>> forgetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO dto,
            BindingResult bindingResult
    ) throws ValidationException, AppServerException {


        if (bindingResult.hasErrors()) {
            LOGGER.warn("Validation errors in password reset request: {}", bindingResult.getAllErrors());
            throw new ValidationException(bindingResult);
        }

        try {
            PasswordResetToken token = passwordResetTokenService.generateTokenForUser(dto.username());
            LOGGER.info("Password reset token: {}", token);
            emailService.sendPasswordResetEmail(dto.username(), token.getToken());
            LOGGER.info("Password reset email sent to user: {}", dto.username());

            return ResponseEntity.ok()
                    .body(new ApiResponseDTO<>(
                            HttpStatus.OK.value(),
                            "Password reset link has been sent to your email",
                            null
                    ));
        } catch (MailException e) {
            LOGGER.error("Failed to send password reset email to: {}", dto.username(), e);
            throw new AppServerException(
                    "EmailSendFailed",
                    "Failed to send password reset email. Please try again later."
            );

        } catch (Exception e) {
            LOGGER.error("Unexpected error during password reset for user: {}", dto.username(), e);
            throw new AppServerException(
                    "PasswordResetFailed",
                    "An error occurred while processing your request. Please try again later."
            );
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponseDTO<Void>> resetPassword(
            @Valid @RequestBody NewPasswordAfterResetRequestDTO dto,
            BindingResult bindingResult
    ) throws ValidationException, AppServerException, AppObjectNotAuthorizedException {

        if (bindingResult.hasErrors()) {
            LOGGER.warn("Invalid password reset request: {}", bindingResult.getAllErrors());
            throw new ValidationException(bindingResult);
        }
        try {
            User user = passwordResetTokenService.getUserForValidToken(dto.token());
            userService.updateUserPasswordAfterReset(user, dto.newPassword());
            return ResponseEntity.ok()
                    .body(new ApiResponseDTO<>(
                            HttpStatus.OK.value(),
                            "Password has been reset successfully",
                            null
                    ));

        } catch (AppObjectNotFoundException e) {
            LOGGER.error("Invalid or expired token: {}", dto.token());
            throw new AppObjectNotAuthorizedException(
                    "Token",
                    "The password reset link is invalid or has expired. Please request a new one."
            );
        } catch (Exception e) {
            LOGGER.error("Error resetting password: {}", e.getMessage(), e);
            throw new AppServerException(
                    "PasswordResetFailed",
                    "An error occurred while resetting your password. Please try again."
            );
        }
    }
}
