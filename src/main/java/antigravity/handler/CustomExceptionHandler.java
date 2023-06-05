package antigravity.handler;

import antigravity.handler.ex.CustomApiException;
import antigravity.handler.ex.CustomValidationException;
import antigravity.model.response.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseDto<String>> MissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.error("MissingServletRequestParameterException() ", ex);
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getParameterName(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto<>(-1, ex.getMessage(), ex.getParameterName()));
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<?> NumberFormatException(NumberFormatException ex) {
        log.error("NumberFormatException() ", ex);
        String errorMessage = "잘못된 요청입니다. 숫자 값을 입력해야 합니다.";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto<>(-1, errorMessage, null));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> MethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("MethodArgumentTypeMismatchException() ", ex);
        String errorMessage = "잘못된 요청입니다. 숫자 값을 입력해야 합니다.";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto<>(-1, errorMessage, null));
    }

    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<ResponseDto<String>> handleValidationException(CustomValidationException ex) {
        log.error("handleValidationException() ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto<>(-1, ex.getMessage(), ex.getParameter()));
    }

    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<?> apiException(CustomApiException ex) {
        log.error("apiException", ex);
        return new ResponseEntity<>(new ResponseDto<>(-1, ex.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<String>> serverException(Exception ex) {
        log.error("serverException", ex);
        return new ResponseEntity<>(new ResponseDto<>(-1, "서버 에러 입니다. 관리자에게 문의해주세요.", null), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
