package gr.aueb.cf.springtaskrest.dto;

import gr.aueb.cf.springtaskrest.core.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskInsertDTO(
        @NotBlank(message = "Title cannot be empty")
        @Size(min = 2, message = "Title must contains at least 2 characters")
        String title,

        @NotBlank(message = "Description cannot be empty")
        @Size(min = 2, message = "Description must contains at least 2 characters")
        String description,

        @NotNull(message = "Status cannot be null")
        TaskStatus status
) {
    public TaskInsertDTO(String title, String description) {
        this(title, description, TaskStatus.OPEN);
    }
}
