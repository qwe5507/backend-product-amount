package antigravity.config.dummy;


import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import antigravity.domain.entity.PromotionProducts;

import java.time.LocalDateTime;

public class DummyObject {
    protected PromotionProducts newPromotionProducts(Product product, Promotion promotion) {
        return PromotionProducts.builder()
                .product(product)
                .promotion(promotion)
                .build();
    }

    protected PromotionProducts newMockPromotionProducts(Integer id, Product product, Promotion promotion) {
        return PromotionProducts.builder()
                .id(id)
                .product(product)
                .promotion(promotion)
                .build();
    }

    protected Promotion newPromotion(String promotionType, String name, String discountType, int discountValue, LocalDateTime useStartedAt, LocalDateTime useEndedAt) {
        return Promotion.builder()
                .promotion_type(promotionType)
                .name(name)
                .discount_type(discountType)
                .discount_value(discountValue)
                .use_started_at(useStartedAt)
                .use_ended_at(useEndedAt)
                .build();
    }

    protected Promotion newMockPromotion(Integer id,String promotionType, String name, String discountType, int discountValue, LocalDateTime useStartedAt, LocalDateTime useEndedAt) {
        return Promotion.builder()
                .id(id)
                .promotion_type(promotionType)
                .name(name)
                .discount_type(discountType)
                .discount_value(discountValue)
                .use_started_at(useStartedAt)
                .use_ended_at(useEndedAt)
                .build();
    }

    protected Product newProduct(String name, int price) {
        return Product.builder()
                .name(name)
                .price(price)
                .build();
    }

    protected Product newMockProduct(Integer id, String name, int price) {
        return Product.builder()
                .id(id)
                .name(name)
                .price(price)
                .build();
    }
}
