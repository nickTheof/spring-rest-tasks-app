package gr.aueb.cf.springtaskrest.core.exceptions;

public class AppServerException extends AppObjectGenericException {
    public AppServerException(String code, String message) {
        super(code, message);
    }

}
