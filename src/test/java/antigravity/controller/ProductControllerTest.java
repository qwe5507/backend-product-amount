package antigravity.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Controller 테스트")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ProductControllerTest {
    @Autowired
    private MockMvc mvc;

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

    @DisplayName("두번째 파라미터가 비었을때, 400에러와 함께 두번째 파라미터명과 실패코드를 던진다.")
    @Test
    public void parameter_validation_empty2_check_test() throws Exception {
        // given
        String productId = "1";

        // when
        ResultActions resultActions = mvc.perform(get("/products/amount")
                .queryParam("product_id", productId));

        // then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.code").value(-1));
        resultActions.andExpect(jsonPath("$.data").value("coupon_id"));
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
}