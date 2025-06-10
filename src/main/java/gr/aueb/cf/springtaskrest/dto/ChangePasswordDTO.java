package gr.aueb.cf.springtaskrest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangePasswordDTO(
        @NotBlank(message = "Old password is required field")
        String oldPassword,
        @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[@#$%!^&*]).{8,}$", message = "Invalid Password")
        String newPassword
) {
}
