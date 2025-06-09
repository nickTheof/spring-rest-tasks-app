package gr.aueb.cf.springtaskrest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRegisterDTO(
        @NotBlank(message = "Username is required field")
        @Email(message = "Invalid format of username")
        String username,

        @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[@#$%!^&*]).{8,}$", message = "Invalid Password")
        String password
) {
}
