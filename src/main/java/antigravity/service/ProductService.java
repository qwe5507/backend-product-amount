package antigravity.service;

import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import antigravity.domain.entity.PromotionProducts;
import antigravity.handler.ex.CustomApiException;
import antigravity.handler.ex.CustomServerException;
import antigravity.model.request.ProductInfoRequest;
import antigravity.model.response.ProductAmountResponse;
import antigravity.repository.ProductRepository;
import antigravity.repository.PromotionProductsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProductService {
    private static final String TYPE_COUPON = "COUPON";
    private static final String TYPE_CODE = "CODE";
    private static final String TYPE_WON = "WON";
    private static final String TYPE_PERCENT = "PERCENT";
    private final ProductRepository productRepository;
    private final PromotionProductsRepository promotionProductsRepository;

    public ProductAmountResponse getProductAmount(ProductInfoRequest request) {
        // 상품 체크
        Product productPS = getProduct(request.getProductId());
        productCheck(productPS);

        int originPrice = productPS.getPrice(); //상품 기존 가격
        int discountPrice = 0; //총 할인 금액
        int finalPrice; //확정 상품 가격

        // couponIds의 값이 존재 할 때만, 할인 진행
        int[] couponIds = request.getCouponIds();

        if (couponIds != null && couponIds.length != 0) {
            List<PromotionProducts> promotionProductList = getPromotionProductList(productPS, couponIds);

            for (PromotionProducts productPromotion : promotionProductList) {
                Promotion promotion = productPromotion.getPromotion();
                promotionCheck(promotion);

                //할인 금액 계산
                discountPrice = calcDiscountPrice(originPrice, discountPrice, promotion);
            }
        }

        // 할인 값 계산
        finalPrice = originPrice - discountPrice;

        // dto 세팅 후 리턴
        return ProductAmountResponse.builder()
                .name(productPS.getName())
                .originPrice(originPrice)
                .discountPrice(discountPrice)
                .finalPrice(finalPrice)
                .build();
    }

    private void productCheck(Product productPS) {
        int price = productPS.getPrice();

        if (price < 10000 || price > 10000000) {
            throw new CustomServerException("잘못된 상품 입니다.");
        }
    }

    private int calcDiscountPrice(int originPrice, int discountPrice, Promotion promotion) {

        String discountType = promotion.getDiscount_type();
        int currentDiscountValue = promotion.getDiscount_value();

        // Type Won 할인 적용
        if (TYPE_WON.equals(discountType)) {
            discountPrice += currentDiscountValue;

            if (discountPrice > originPrice) {
                throw new CustomApiException("적용할 수 없는 쿠폰입니다.");
            }
        }

        // Type Percent 할인 적용
        if (TYPE_PERCENT.equals(discountType)) {

            // 할인 금액 구하기
            // 할인금액 = 원금 x (할인율 / 100)
            // KRW이기 때문에 소수점 제거
            int percentDiscountPrice = new BigDecimal(originPrice).multiply(new BigDecimal(currentDiscountValue).divide(new BigDecimal("100")))
                    .setScale(0, RoundingMode.DOWN).intValue();

            discountPrice += percentDiscountPrice;

            if (discountPrice > originPrice) {
                throw new CustomApiException("적용할 수 없는 쿠폰입니다.");
            }
        }

        return discountPrice;
    }

    private void promotionCheck(Promotion promotion) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(promotion.getUse_started_at()) || now.isAfter(promotion.getUse_ended_at())) { //오늘이 유효시간 체크
            throw new CustomApiException("쿠폰이 유효하지 않습니다.");
        }

        String promotionType = promotion.getPromotion_type();
        String discountType = promotion.getDiscount_type();

        // promotion 타입 검증 (server error)
        if (!TYPE_COUPON.equals(promotionType) && !TYPE_CODE.equals(promotionType)) {
            throw new CustomServerException("올바르지 않은 promotion Type입니다.");
        }

        // discount 타입 검증 (server error)
        if (TYPE_COUPON.equals(promotionType) && !TYPE_WON.equals(discountType)) {
            throw new CustomServerException("올바르지 않은 discount Type입니다.");
        }

        if (TYPE_CODE.equals(promotionType) && !TYPE_PERCENT.equals(discountType)) {
            throw new CustomServerException("올바르지 않은 discount Type입니다.");
        }

        int currentDiscountValue = promotion.getDiscount_value();

        // Type Percent 할인 적용
        if (TYPE_PERCENT.equals(discountType)) {
            // Percent 타입
            if (currentDiscountValue < 1 || currentDiscountValue > 100) {
                throw new CustomServerException("올바르지 않은 쿠폰입니다.");
            }
        }
    }

    private List<PromotionProducts> getPromotionProductList(Product productPS, int[] couponIds) {
        List<Integer> promotionIdList = Arrays.stream(couponIds)
                .boxed()
                .collect(Collectors.toList());
        List<PromotionProducts> productPromotionPSList = promotionProductsRepository.findByProductAndPromotionIds(productPS, promotionIdList);

        if (promotionIdList.size() != productPromotionPSList.size()) {
            throw new CustomApiException("해당 쿠폰이 없습니다.");
        }
        return productPromotionPSList;
    }

    private Product getProduct(int productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CustomApiException("해당 상품이 없습니다."));
    }
}