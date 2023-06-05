package antigravity.handler.ex;

import lombok.Getter;


@Getter
public class CustomServerException extends RuntimeException {
    public CustomServerException(String message) {
        super(message);
    }
}
