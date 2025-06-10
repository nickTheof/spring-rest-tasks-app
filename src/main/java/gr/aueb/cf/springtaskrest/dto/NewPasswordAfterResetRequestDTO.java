package gr.aueb.cf.springtaskrest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record NewPasswordAfterResetRequestDTO(
        @NotBlank(message = "token cannot be empty")
        String token,

        @NotBlank(message = "new password is required field")
        @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[@#$%!^&*]).{8,}$", message = "Invalid Password")
        String newPassword

) {
}
