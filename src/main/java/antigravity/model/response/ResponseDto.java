package antigravity.model.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
    Response포맷을 정해주셔서, validation error 및 실패 시의 사용되는 Response 포맷
 */
@RequiredArgsConstructor
@Getter
public class ResponseDto<T> {
    private final Integer code; // 1 성공, -1 실패
    private final String msg;
    private final T data;
}

