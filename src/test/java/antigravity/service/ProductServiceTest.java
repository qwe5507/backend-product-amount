package antigravity.service;

import antigravity.config.dummy.DummyObject;
import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import antigravity.domain.entity.PromotionProducts;
import antigravity.handler.ex.CustomApiException;
import antigravity.handler.ex.CustomServerException;
import antigravity.model.request.ProductInfoRequest;
import antigravity.model.response.ProductAmountResponse;
import antigravity.repository.ProductRepository;
import antigravity.repository.PromotionProductsRepository;
import antigravity.repository.PromotionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Service 테스트")
@ExtendWith(MockitoExtension.class)
class ProductServiceTest extends DummyObject {
    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PromotionRepository promotionRepository;
    @Mock
    private PromotionProductsRepository promotionProductsRepository;

    @DisplayName("금액 조회 시, 상품이 등록되어 있지 않으면 에러 발생")
    @Test
    void product_empty_test() {
        // given
        int[] ids = {1, 2};
        ProductInfoRequest req = ProductInfoRequest.builder().productId(1).couponIds(ids).build();

        // stub
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        // when
        CustomApiException exception = assertThrows(CustomApiException.class, () -> {
            productService.getProductAmount(req);
        });

        // then
        assertThat(exception.getMessage()).isEqualTo("해당 상품이 없습니다.");
    }

    @DisplayName("금액 조회 시, 상품의 금액이 10000이하면 에러 발생")
    @Test
    void product_price_check_test() {
        // given
        int[] ids = {1, 2};
        ProductInfoRequest req = ProductInfoRequest.builder().productId(1).couponIds(ids).build();

        // stub
        Product product1 = newMockProduct(1, "상품", 1000);
        when(productRepository.findById(any())).thenReturn(Optional.of(product1));

        // when
        CustomServerException exception = assertThrows(CustomServerException.class, () -> {
            productService.getProductAmount(req);
        });

        // then
        assertThat(exception.getMessage()).isEqualTo("잘못된 상품 입니다.");
    }

    @DisplayName("금액 조회 시, 상품의 금액이 10000000이상이면 에러 발생")
    @Test
    void product_price_check2_test() {
        // given
        int[] ids = {1, 2};
        ProductInfoRequest req = ProductInfoRequest.builder().productId(1).couponIds(ids).build();

        // stub
        Product product1 = newMockProduct(1, "상품", 15000000);
        when(productRepository.findById(any())).thenReturn(Optional.of(product1));

        // when
        CustomServerException exception = assertThrows(CustomServerException.class, () -> {
            productService.getProductAmount(req);
        });

        // then
        assertThat(exception.getMessage()).isEqualTo("잘못된 상품 입니다.");
    }

    @DisplayName("금액 조회 시, 상품과 매핑된 쿠폰이 없으면 에러 발생")
    @Test
    void promotion_product_check_test() {
        // given
        int[] ids = {1, 2};
        ProductInfoRequest req = ProductInfoRequest.builder().productId(1).couponIds(ids).build();

        // stub1
        Product product1 = newMockProduct(1, "상품", 100000);
        when(productRepository.findById(any())).thenReturn(Optional.of(product1));

        // stub2
        when(promotionProductsRepository.findByProductAndPromotionIds(any(), any())).thenReturn(new ArrayList<>());

        // when
        CustomApiException exception = assertThrows(CustomApiException.class, () -> {
            productService.getProductAmount(req);
        });

        // then
        assertThat(exception.getMessage()).isEqualTo("해당 쿠폰이 없습니다.");
    }

    @DisplayName("금액 조회 시, 쿠폰의 유효기간이 지나면 에러가 발생")
    @Test
    void promotion_date_check_test() {
        // given
        int[] ids = {1, 2};
        ProductInfoRequest req = ProductInfoRequest.builder().productId(1).couponIds(ids).build();

        // stub1
        Product product1 = newMockProduct(1, "상품", 100000);
        when(productRepository.findById(any())).thenReturn(Optional.of(product1));

        // stub2
        Promotion promotion1 = newMockPromotion(1, "COUPON", "이상한 5000원 할인 쿠폰", "WON", 5000, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(10));
        Promotion promotion2 = newMockPromotion(2, "CODE", "이상한 10%할인 쿠폰", "PERCENT", 10, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(10));
        PromotionProducts promotionProduct1 = newMockPromotionProducts(1, product1, promotion1);
        PromotionProducts promotionProduct2 = newMockPromotionProducts(2, product1, promotion2);
        when(promotionProductsRepository.findByProductAndPromotionIds(any(), any())).thenReturn(List.of(promotionProduct1, promotionProduct2));

        // when
        CustomApiException exception = assertThrows(CustomApiException.class, () -> {
            productService.getProductAmount(req);
        });

        // then
        assertThat(exception.getMessage()).isEqualTo("쿠폰이 유효하지 않습니다.");
    }

    @DisplayName("금액 조회 시, 쿠폰의 프로모션 타입이 잘못되어있으면 서버에러가 발생")
    @Test
    void promotion_promotion_type_check_test() {
        // given
        int[] ids = {1, 2};
        ProductInfoRequest req = ProductInfoRequest.builder().productId(1).couponIds(ids).build();

        // stub1
        Product product1 = newMockProduct(1, "상품", 100000);
        when(productRepository.findById(any())).thenReturn(Optional.of(product1));

        // stub2
        Promotion promotion1 = newMockPromotion(1, "INVALID", "5000원 할인 쿠폰", "WON", 5000, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        Promotion promotion2 = newMockPromotion(2, "INVALID", "10%할인 쿠폰", "PERCENT", 10, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        PromotionProducts promotionProduct1 = newMockPromotionProducts(1, product1, promotion1);
        PromotionProducts promotionProduct2 = newMockPromotionProducts(2, product1, promotion2);
        when(promotionProductsRepository.findByProductAndPromotionIds(any(), any())).thenReturn(List.of(promotionProduct1, promotionProduct2));

        // when
        CustomServerException exception = assertThrows(CustomServerException.class, () -> {
            productService.getProductAmount(req);
        });

        // then
        assertThat(exception.getMessage()).isEqualTo("올바르지 않은 promotion Type입니다.");
    }

    @DisplayName("금액 조회 시, 쿠폰 타입이 'COUPON' 일 때, 할인 타입이 'WON'이 아니면 서버에러가 발생")
    @Test
    void promotion_discount_type_check1_test() {
        // given
        int[] ids = {1, 2};
        ProductInfoRequest req = ProductInfoRequest.builder().productId(1).couponIds(ids).build();

        // stub1
        Product product1 = newMockProduct(1, "상품", 100000);
        when(productRepository.findById(any())).thenReturn(Optional.of(product1));

        // stub2
        Promotion promotion1 = newMockPromotion(1, "COUPON", "5000원 할인 쿠폰", "INVALID", 5000, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        Promotion promotion2 = newMockPromotion(2, "CODE", "10%할인 쿠폰", "PERCENT", 10, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        PromotionProducts promotionProduct1 = newMockPromotionProducts(1, product1, promotion1);
        PromotionProducts promotionProduct2 = newMockPromotionProducts(2, product1, promotion2);
        when(promotionProductsRepository.findByProductAndPromotionIds(any(), any())).thenReturn(List.of(promotionProduct1, promotionProduct2));

        // when
        CustomServerException exception = assertThrows(CustomServerException.class, () -> {
            productService.getProductAmount(req);
        });

        // then
        assertThat(exception.getMessage()).isEqualTo("올바르지 않은 discount Type입니다.");
    }

    @DisplayName("금액 조회 시, 쿠폰 타입이 'CODE' 일 때, 할인 타입이 'PERCENT'가 아니면 서버에러가 발생")
    @Test
    void promotion_discount_type_check2_test() {
        // given
        int[] ids = {1, 2};
        ProductInfoRequest req = ProductInfoRequest.builder().productId(1).couponIds(ids).build();

        // stub1
        Product product1 = newMockProduct(1, "상품", 100000);
        when(productRepository.findById(any())).thenReturn(Optional.of(product1));

        // stub2
        Promotion promotion1 = newMockPromotion(1, "COUPON", "5000원 할인 쿠폰", "WON", 5000, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        Promotion promotion2 = newMockPromotion(2, "CODE", "10%할인 쿠폰", "INVALID", 10, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        PromotionProducts promotionProduct1 = newMockPromotionProducts(1, product1, promotion1);
        PromotionProducts promotionProduct2 = newMockPromotionProducts(2, product1, promotion2);
        when(promotionProductsRepository.findByProductAndPromotionIds(any(), any())).thenReturn(List.of(promotionProduct1, promotionProduct2));

        // when
        CustomServerException exception = assertThrows(CustomServerException.class, () -> {
            productService.getProductAmount(req);
        });

        // then
        assertThat(exception.getMessage()).isEqualTo("올바르지 않은 discount Type입니다.");
    }

    @DisplayName("금액 조회 시, 할인 타입이 'PERCENT' 일 때 할인율이 0이면 에러 발생")
    @Test
    void promotion_discount_type_check3_test() {
        // given
        int[] ids = {1, 2};
        ProductInfoRequest req = ProductInfoRequest.builder().productId(1).couponIds(ids).build();

        // stub1
        Product product1 = newMockProduct(1, "상품", 100000);
        when(productRepository.findById(any())).thenReturn(Optional.of(product1));

        // stub2
        Promotion promotion1 = newMockPromotion(1, "COUPON", "5000원 할인 쿠폰", "WON", 5000, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        Promotion promotion2 = newMockPromotion(2, "CODE", "10%할인 쿠폰", "PERCENT", 0, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        PromotionProducts promotionProduct1 = newMockPromotionProducts(1, product1, promotion1);
        PromotionProducts promotionProduct2 = newMockPromotionProducts(2, product1, promotion2);
        when(promotionProductsRepository.findByProductAndPromotionIds(any(), any())).thenReturn(List.of(promotionProduct1, promotionProduct2));

        // when
        CustomServerException exception = assertThrows(CustomServerException.class, () -> {
            productService.getProductAmount(req);
        });

        // then
        assertThat(exception.getMessage()).isEqualTo("올바르지 않은 쿠폰입니다.");
    }

    @DisplayName("금액 조회 시,'COUPON'타입인 쿠폰이 하나 일 때, 해당 쿠폰이 원가를 초과 하면 사용 할수 없어 에러 발생")
    @Test
    void promotion_discount_amount_check_test() {
        // given
        int[] ids = {1};
        ProductInfoRequest req = ProductInfoRequest.builder().productId(1).couponIds(ids).build();

        // stub1
        Product product1 = newMockProduct(1, "상품", 100000);
        when(productRepository.findById(any())).thenReturn(Optional.of(product1));

        // stub2
        Promotion promotion1 = newMockPromotion(1, "COUPON", "초대박 할인 쿠폰", "WON", 100000000, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        PromotionProducts promotionProduct1 = newMockPromotionProducts(1, product1, promotion1);
        when(promotionProductsRepository.findByProductAndPromotionIds(any(), any())).thenReturn(List.of(promotionProduct1));

        // when
        CustomApiException exception = assertThrows(CustomApiException.class, () -> {
            productService.getProductAmount(req);
        });

        // then
        assertThat(exception.getMessage()).isEqualTo("적용할 수 없는 쿠폰입니다.");
    }

    @DisplayName("금액 조회 시,'COUPON'타입인 쿠폰이 두 개 일 때, 쿠폰들의 할인 합산 금액이 원가를 초과 하면 사용 할수 없어 에러 발생")
    @Test
    void promotion_discount_amount_check2_test() {
        // given
        int[] ids = {1, 2};
        ProductInfoRequest req = ProductInfoRequest.builder().productId(1).couponIds(ids).build();

        // stub1
        Product product1 = newMockProduct(1, "상품", 100000);
        when(productRepository.findById(any())).thenReturn(Optional.of(product1));

        // stub2
        Promotion promotion1 = newMockPromotion(1, "COUPON", "일반 할인 쿠폰", "WON", 50000, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        Promotion promotion2 = newMockPromotion(2, "COUPON", "일반 할인 쿠폰", "WON", 70000, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        PromotionProducts promotionProduct1 = newMockPromotionProducts(1, product1, promotion1);
        PromotionProducts promotionProduct2 = newMockPromotionProducts(2, product1, promotion2);
        when(promotionProductsRepository.findByProductAndPromotionIds(any(), any())).thenReturn(List.of(promotionProduct1, promotionProduct2));

        // when
        CustomApiException exception = assertThrows(CustomApiException.class, () -> {
            productService.getProductAmount(req);
        });

        // then
        assertThat(exception.getMessage()).isEqualTo("적용할 수 없는 쿠폰입니다.");
    }

    @DisplayName("금액 조회 시,'CODE'타입, 'PERCENT'쿠폰 두 개가 있을 떄, 쿠폰의 할인 합산 금액이 원가를 초과 하면 사용 할수 없어 에러 발생")
    @Test
    void promotion_discount_amount_check3_test() {
        // given
        int[] ids = {1, 2};
        ProductInfoRequest req = ProductInfoRequest.builder().productId(1).couponIds(ids).build();

        // stub1
        Product product1 = newMockProduct(1, "상품", 100000);
        when(productRepository.findById(any())).thenReturn(Optional.of(product1));

        // stub2
        Promotion promotion1 = newMockPromotion(1, "COUPON", "일반 할인 쿠폰", "WON", 50000, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        Promotion promotion2 = newMockPromotion(2, "CODE", "95%할인 쿠폰", "PERCENT", 95, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        PromotionProducts promotionProduct1 = newMockPromotionProducts(1, product1, promotion1);
        PromotionProducts promotionProduct2 = newMockPromotionProducts(2, product1, promotion2);
        when(promotionProductsRepository.findByProductAndPromotionIds(any(), any())).thenReturn(List.of(promotionProduct1, promotionProduct2));

        // when
        CustomApiException exception = assertThrows(CustomApiException.class, () -> {
            productService.getProductAmount(req);
        });

        // then
        assertThat(exception.getMessage()).isEqualTo("적용할 수 없는 쿠폰입니다.");
    }

    @DisplayName("금액 조회 시, 쿠폰이 없으면 원금액 그대로 리턴")
    @Test
    void promotion_no_coupon_test() {
        // given
        ProductInfoRequest req = ProductInfoRequest.builder().productId(1).build();

        // stub1
        Product product1 = newMockProduct(1, "상품", 100000);
        when(productRepository.findById(any())).thenReturn(Optional.of(product1));

        // when
        ProductAmountResponse productAmount = productService.getProductAmount(req);

        // then
        assertThat(productAmount.getName()).isEqualTo("상품");
        assertThat(productAmount.getOriginPrice()).isEqualTo(100000);
        assertThat(productAmount.getDiscountPrice()).isEqualTo(0);
        assertThat(productAmount.getFinalPrice()).isEqualTo(100000);
    }

    @DisplayName("금액 조회 시, 'WON' 타입의 쿠폰 적용 시 discount_value 만큼이 할인되어 리턴")
    @Test
    void promotion_won_coupon_test() {
        // given
        int[] ids = {1};
        ProductInfoRequest req = ProductInfoRequest.builder().productId(1).couponIds(ids).build();

        // stub1
        Product product1 = newMockProduct(1, "상품", 100000);
        when(productRepository.findById(any())).thenReturn(Optional.of(product1));

        // stub2
        Promotion promotion1 = newMockPromotion(1, "COUPON", "일반 할인 쿠폰", "WON", 10000, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        PromotionProducts promotionProduct1 = newMockPromotionProducts(1, product1, promotion1);
        when(promotionProductsRepository.findByProductAndPromotionIds(any(), any())).thenReturn(List.of(promotionProduct1));

        // when
        ProductAmountResponse productAmount = productService.getProductAmount(req);

        // then
        assertThat(productAmount.getName()).isEqualTo("상품");
        assertThat(productAmount.getOriginPrice()).isEqualTo(100000);
        assertThat(productAmount.getDiscountPrice()).isEqualTo(10000);
        assertThat(productAmount.getFinalPrice()).isEqualTo(90000);
    }

    @DisplayName("금액 조회 시, 'PERCENT' 타입의 쿠폰 적용 시 discount_value의 할인율이 원가에서 할인되어 리턴")
    @Test
    void promotion_percent_coupon_test() {
        // given
        int[] ids = {1};
        ProductInfoRequest req = ProductInfoRequest.builder().productId(1).couponIds(ids).build();

        // stub1
        Product product1 = newMockProduct(1, "상품", 100000);
        when(productRepository.findById(any())).thenReturn(Optional.of(product1));

        // stub2
        Promotion promotion1 = newMockPromotion(1, "CODE", "일반 할인 쿠폰", "PERCENT", 8, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        PromotionProducts promotionProduct1 = newMockPromotionProducts(1, product1, promotion1);
        when(promotionProductsRepository.findByProductAndPromotionIds(any(), any())).thenReturn(List.of(promotionProduct1));

        // when
        ProductAmountResponse productAmount = productService.getProductAmount(req);

        // then
        assertThat(productAmount.getName()).isEqualTo("상품");
        assertThat(productAmount.getOriginPrice()).isEqualTo(100000);
        assertThat(productAmount.getDiscountPrice()).isEqualTo(8000);
        assertThat(productAmount.getFinalPrice()).isEqualTo(92000);
    }

    @DisplayName("금액 조회 시, 'PERCENT' 타입과 'WON'타입의 쿠폰 동시에 적용 시 합산된 할인 금액이 리턴")
    @Test
    void promotion_mix_coupon_test() {
        // given
        int[] ids = {1, 2};
        ProductInfoRequest req = ProductInfoRequest.builder().productId(1).couponIds(ids).build();

        // stub1
        Product product1 = newMockProduct(1, "상품", 100000);
        when(productRepository.findById(any())).thenReturn(Optional.of(product1));

        // stub2
        Promotion promotion1 = newMockPromotion(1, "COUPON", "일반 할인 쿠폰", "WON", 50000, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        Promotion promotion2 = newMockPromotion(2, "CODE", "8% 할인 쿠폰", "PERCENT", 8, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        PromotionProducts promotionProduct1 = newMockPromotionProducts(1, product1, promotion1);
        PromotionProducts promotionProduct2 = newMockPromotionProducts(2, product1, promotion2);
        when(promotionProductsRepository.findByProductAndPromotionIds(any(), any())).thenReturn(List.of(promotionProduct1, promotionProduct2));

        // when
        ProductAmountResponse productAmount = productService.getProductAmount(req);

        // then
        assertThat(productAmount.getName()).isEqualTo("상품");
        assertThat(productAmount.getOriginPrice()).isEqualTo(100000);
        assertThat(productAmount.getDiscountPrice()).isEqualTo(58000);
        assertThat(productAmount.getFinalPrice()).isEqualTo(42000);
    }
}