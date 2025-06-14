package gr.aueb.cf.springtaskrest.dto;


import java.util.List;

public record TaskFiltersDTO(
        Integer page,
        Integer size,
        String sortBy,
        String orderBy,
        String uuid,
        String title,
        List<String> taskStatus,
        Boolean userIsActive,
        String userUuid
) {
    public TaskFiltersDTO() {
        this(null, null, null, null,null, null, null, null, null);
    }

    public TaskFiltersDTO(Integer page, Integer size) {
        this(page, size, null, null, null, null,null,null, null);
    }

    public TaskFiltersDTO(Integer page, Integer size, String userUuid) {
        this(page, size, null, null, null, null,null,null, userUuid);
    }

    public TaskFiltersDTO(String userUuid) {
        this(null, null, null, null, null, null,null,null, userUuid);
    }

    public TaskFiltersDTO(TaskFiltersDTO dto, String userUuid) {
        this(dto.page, dto.size, dto.sortBy, dto.orderBy, dto.uuid, dto.title, dto.taskStatus, dto.userIsActive, userUuid);
    }
}
