package gr.aueb.cf.springtaskrest.core.exceptions;

public class AppObjectAlreadyExistsException extends AppObjectGenericException {
    private static final String DEFAULT_CODE = "AlreadyExists";

    public AppObjectAlreadyExistsException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }

}
