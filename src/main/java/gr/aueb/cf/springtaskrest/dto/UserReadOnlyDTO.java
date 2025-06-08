package gr.aueb.cf.springtaskrest.dto;

public record UserReadOnlyDTO(
        Long id,
        String uuid,
        String username,
        Boolean isActive,
        String role
) {
}
