package gr.aueb.cf.springtaskrest.dto;

import java.util.Map;

public record ApiErrorDTO(
        String code,
        String message,
        Map<String, String> errors, // null if not a validation error
        long timestamp,
        String path // optional, can be null
) {
    public ApiErrorDTO(String code, String message, long timestamp, String path) {
        this(code, message, Map.of(), timestamp, path);
    }
}
