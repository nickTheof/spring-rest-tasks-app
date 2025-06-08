package gr.aueb.cf.springtaskrest.dto;

public record ResponseMessageDTO(
    String code,
    String description
) {
    public ResponseMessageDTO(String code) {
        this(code, "");
    }
}
