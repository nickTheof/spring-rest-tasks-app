package gr.aueb.cf.springtaskrest.core.exceptions;

public class AppObjectNotAuthorizedException extends AppObjectGenericException {
    private static final String DEFAULT_CODE = "NotAuthorized";

    public AppObjectNotAuthorizedException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }

}
