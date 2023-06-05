package antigravity.controller;

import antigravity.config.dummy.DummyObject;
import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import antigravity.repository.ProductRepository;
import antigravity.repository.PromotionProductsRepository;
import antigravity.repository.PromotionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("classpath:db/teardown.sql")
@DisplayName("Controller 테스트")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ProductControllerTest extends DummyObject {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private PromotionProductsRepository promotionProductsRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        dataSetting();
        em.clear();
    }

    @DisplayName("파라미터가 비었을때, 400에러와 함께 실패코드를 던진다.")
    @Test
    public void parameter_validation_empty_check_test() throws Exception {
        // given
        // when
        ResultActions resultActions = mvc.perform(get("/products/amount"));

        // then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.code").value(-1));
        resultActions.andExpect(jsonPath("$.data").value("product_id"));
    }

    @DisplayName("두번째 파라미터인 CouponId는 비어도 정상급액, 동일한 할인 금액을 리턴한다")
    @Test
    public void parameter_validation_empty2_check_test() throws Exception {
        // given
        String productId = "1";

        // when
        ResultActions resultActions = mvc.perform(get("/products/amount")
                .queryParam("product_id", productId));

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.name").value("panties"));
        resultActions.andExpect(jsonPath("$.originPrice").value(100000));
        resultActions.andExpect(jsonPath("$.discountPrice").value(0));
        resultActions.andExpect(jsonPath("$.finalPrice").value(100000));
    }

    @DisplayName("파라미터가 숫자가 아닐때, 400에러와 함께 실패코드를 던진다.")
    @Test
    public void parameter_validation_value_check_test() throws Exception {
        // given
        String productId = "1";
        String couponId = "test";

        // when
        ResultActions resultActions = mvc.perform(get("/products/amount")
                .queryParam("product_id", productId)
                .queryParam("coupon_id", couponId));

        // then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.code").value(-1));
        resultActions.andExpect(jsonPath("$.msg").value("잘못된 요청입니다. 숫자 값을 입력해야 합니다."));
    }

    @DisplayName("파라미터가 1이상이 아닐 떄, 400에러와 함께 실패코드를 던진다.")
    @Test
    public void parameter_validation_minus_check_test() throws Exception {
        // given
        String productId = "1";
        String couponId = "1, -1";

        // when
        ResultActions resultActions = mvc.perform(get("/products/amount")
                .queryParam("product_id", productId)
                .queryParam("coupon_id", couponId));

        // then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.code").value(-1));
        resultActions.andExpect(jsonPath("$.msg").value("couponId는 1이상 이어야 합니다."));
        resultActions.andExpect(jsonPath("$.data").value("couponId"));
    }

    private void dataSetting() {
        Product productPS = productRepository.save(newProduct("panties", 100000));
        //사용가능한, Coupon 프로모션
        Promotion promotionCouponAvailablePS = promotionRepository.save(newPromotion("COUPON", "5000원 할인 쿠폰", "WON", 5000
                , LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2)));
        //사용가능한, Code 프로모션
        Promotion promotionCodeAvailablePS = promotionRepository.save(newPromotion("CODE", "10%할인 쿠폰", "PERCENT", 10
                , LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2)));
        //사용 불가능한, Coupon 프로모션
        Promotion promotionCouponDisabledPS = promotionRepository.save(newPromotion("COUPON", "이상한 10%할인 쿠폰", "WON", 5000
                , LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(10)));
        //사용 불가능한, Code 프로모션
        Promotion promotionCodeDisabledPS = promotionRepository.save(newPromotion("CODE", "이상한 10%할인 쿠폰", "PERCENT", 10
                , LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(10)));

        promotionProductsRepository.save(newPromotionProducts(productPS, promotionCouponAvailablePS));
        promotionProductsRepository.save(newPromotionProducts(productPS, promotionCodeAvailablePS));
        promotionProductsRepository.save(newPromotionProducts(productPS, promotionCouponDisabledPS));
        promotionProductsRepository.save(newPromotionProducts(productPS, promotionCodeDisabledPS));
    }
}