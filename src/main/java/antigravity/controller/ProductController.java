package antigravity.controller;

import antigravity.handler.ex.CustomValidationException;
import antigravity.model.request.ProductInfoRequest;
import antigravity.model.response.ProductAmountResponse;
import antigravity.model.response.ResponseDto;
import antigravity.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    //상품 가격 추출 api
    @GetMapping("/amount")
    public ResponseEntity<ProductAmountResponse> getProductAmount(
            @RequestParam("product_id") int productId,
            @RequestParam("coupon_id") int[] couponIds
    ) {

        parameterValidation(productId, couponIds);

        ProductAmountResponse response = productService.getProductAmount(getParam(productId, couponIds));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void parameterValidation(int productId, int[] couponIds) {
        if (productId < 1) {
            throw new CustomValidationException("productId", "productId는 1이상 이어야 합니다.");
        }
        boolean invalidCouponIdExists = Arrays.stream(couponIds)
                .anyMatch(couponId -> couponId < 1);

        if (invalidCouponIdExists) {
            throw new CustomValidationException("couponId", "couponId는 1이상 이어야 합니다.");
        }
    }

    private ProductInfoRequest getParam(int productId, int[] couponIds) {
        ProductInfoRequest request = ProductInfoRequest.builder()
                .productId(productId)
                .couponIds(couponIds)
                .build();

        return request;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseDto<String>> handleMissingParameterException(MissingServletRequestParameterException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getParameterName(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto<>(-1, ex.getMessage(), ex.getParameterName()));
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatch(NumberFormatException ex) {
        String errorMessage = "잘못된 요청입니다. 숫자 값을 입력해야 합니다.";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto<>(-1, errorMessage, null));
    }

    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<ResponseDto<String>> handleValidationException(CustomValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto<>(-1, ex.getMessage(), ex.getParameter()));
    }
}
