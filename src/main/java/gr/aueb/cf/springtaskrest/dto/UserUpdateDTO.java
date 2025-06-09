package gr.aueb.cf.springtaskrest.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record UserUpdateDTO(
        @Email(message = "Invalid format of username")
        String username,

        @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[@#$%!^&*]).{8,}$", message = "Invalid Password")
        String password,

        Boolean isActive,

        @Pattern(regexp = "^(ADMIN|USER)$", message = "Role can be ADMIN or USER")
        String role
) {
}
