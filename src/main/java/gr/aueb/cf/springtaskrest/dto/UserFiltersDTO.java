package gr.aueb.cf.springtaskrest.dto;

public record UserFiltersDTO(
    Integer page,
    Integer size,
    String sortBy,
    String orderBy,
    String uuid,
    String username,
    String role,
    Boolean active
) {
    public UserFiltersDTO() {
        this(null, null, null, null, null, null, null, null);
    }
}
