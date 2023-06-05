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
            @RequestParam(value = "coupon_id", required = false) int[] couponIds
    ) {
        log.info("product_id : {}", productId);
        log.info("coupon_id : {}", couponIds);
        parameterValidation(productId, couponIds);

        ProductAmountResponse response = productService.getProductAmount(getParam(productId, couponIds));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void parameterValidation(int productId, int[] couponIds) {
        if (productId < 1) {
            throw new CustomValidationException("productId", "productId는 1이상 이어야 합니다.");
        }

        if (couponIds != null) {
            boolean invalidCouponIdExists = Arrays.stream(couponIds)
                    .anyMatch(couponId -> couponId < 1);

            if (invalidCouponIdExists) {
                throw new CustomValidationException("couponId", "couponId는 1이상 이어야 합니다.");
            }
        }
    }

    private ProductInfoRequest getParam(int productId, int[] couponIds) {
        ProductInfoRequest request = ProductInfoRequest.builder()
                .productId(productId)
                .couponIds(couponIds)
                .build();

        return request;
    }
}
