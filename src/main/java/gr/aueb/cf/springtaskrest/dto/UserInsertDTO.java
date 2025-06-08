package gr.aueb.cf.springtaskrest.dto;

import gr.aueb.cf.springtaskrest.core.enums.Role;
import jakarta.validation.constraints.*;

public record UserInsertDTO(
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
    public UserInsertDTO(String username, String password) {
        this(username, password, true, Role.USER);
    }
}
