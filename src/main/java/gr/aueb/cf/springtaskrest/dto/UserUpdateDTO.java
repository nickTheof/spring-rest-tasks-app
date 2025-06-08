package gr.aueb.cf.springtaskrest.dto;

import gr.aueb.cf.springtaskrest.core.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserUpdateDTO(
        @NotBlank(message = "Username is required field")
        @Email(message = "Invalid format of username")
        String username,

        @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[@#$%!^&*]).{8,}$", message = "Invalid Password")
        String password,

        @NotNull(message = "isActive field is required")
        Boolean isActive,

        @NotNull(message = "Role is required field")
        @Pattern(regexp = "^(ADMIN|USER)$", message = "Role can be ADMIN or USER")
        Role role
) {
}
