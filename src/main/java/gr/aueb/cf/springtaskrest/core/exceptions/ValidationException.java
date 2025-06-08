package gr.aueb.cf.springtaskrest.core.exceptions;

import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class ValidationException extends Exception {
    private BindingResult bindingResult;

    public ValidationException(BindingResult bindingResult) {
      super("Validation failed");
      this.bindingResult = bindingResult;
    }
}
