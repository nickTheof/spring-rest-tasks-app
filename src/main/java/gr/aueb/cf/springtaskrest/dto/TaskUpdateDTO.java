package gr.aueb.cf.springtaskrest.dto;
import jakarta.validation.constraints.Pattern;


public record TaskUpdateDTO(
        String title,
        String description,
        @Pattern(regexp = "^(OPEN|ONGOING|COMPLETED|FAILED|CANCELLED)$")
        String status
) {
}
