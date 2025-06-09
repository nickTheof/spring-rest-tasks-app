package gr.aueb.cf.springtaskrest.core;

import gr.aueb.cf.springtaskrest.core.exceptions.*;
import gr.aueb.cf.springtaskrest.dto.ApiErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorDTO> handleValidationException(ValidationException e, HttpServletRequest request) {
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ApiErrorDTO errorDTO = new ApiErrorDTO(
                "ValidationException", e.getMessage(), errors, System.currentTimeMillis(), request.getRequestURI()
                );
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AppObjectNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiErrorDTO> handleConstraintViolationException(AppObjectNotFoundException e, HttpServletRequest request) {
        return new ResponseEntity<>(new ApiErrorDTO(e.getCode(), e.getMessage(), System.currentTimeMillis(), request.getRequestURI()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({AppObjectAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiErrorDTO> handleConstraintViolationException(AppObjectAlreadyExistsException e, HttpServletRequest request) {
        return new ResponseEntity<>(new ApiErrorDTO(e.getCode(), e.getMessage(), System.currentTimeMillis(), request.getRequestURI()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({AppObjectInvalidArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorDTO> handleConstraintViolationException(AppObjectInvalidArgumentException e, HttpServletRequest request) {
        return new ResponseEntity<>(new ApiErrorDTO(e.getCode(), e.getMessage(), System.currentTimeMillis(), request.getRequestURI()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AppObjectNotAuthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiErrorDTO> handleConstraintViolationException(AppObjectNotAuthorizedException e, HttpServletRequest request) {
        return new ResponseEntity<>(new ApiErrorDTO(e.getCode(), e.getMessage(), System.currentTimeMillis(), request.getRequestURI()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({AppServerException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiErrorDTO> handleConstraintViolationException(AppServerException e, HttpServletRequest request) {
        return new ResponseEntity<>(new ApiErrorDTO(e.getCode(), e.getMessage(), System.currentTimeMillis(), request.getRequestURI()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiErrorDTO> handleConstraintViolationException(Exception e, HttpServletRequest request) {
        return new ResponseEntity<>(new ApiErrorDTO("AppServerException", e.getMessage(), System.currentTimeMillis(), request.getRequestURI()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
