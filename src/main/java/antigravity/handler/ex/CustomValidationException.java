package antigravity.handler.ex;

import lombok.Getter;


@Getter
public class CustomValidationException extends RuntimeException {
    private String parameter;
    public CustomValidationException(String parameter, String message) {
        super(message);
        this.parameter = parameter;
    }
}
