package gr.aueb.cf.springtaskrest.dto;

public record TaskReadOnlyDTO(
        Long id,
        String uuid,
        String title,
        String description,
        String status,
        UserReadOnlyDTO user
) {
}
