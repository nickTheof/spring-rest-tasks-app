package gr.aueb.cf.springtaskrest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequestDTO(
        @NotBlank(message = "Username cannot be empty")
        @Email(message = "Username must be a valid email")
        String username
) {
}
