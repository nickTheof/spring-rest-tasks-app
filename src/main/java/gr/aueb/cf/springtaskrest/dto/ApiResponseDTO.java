package gr.aueb.cf.springtaskrest.dto;

public record ApiResponseDTO<T>(
        Integer Status,
        String message,
        T data
) {}
