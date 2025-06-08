package gr.aueb.cf.springtaskrest.core.exceptions;

public class AppObjectInvalidArgumentException extends AppObjectGenericException {
    private static final String DEFAULT_CODE = "InvalidArgument";

    public AppObjectInvalidArgumentException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }

}
