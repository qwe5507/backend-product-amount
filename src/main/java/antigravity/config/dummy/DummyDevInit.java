package antigravity.config.dummy;

import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import antigravity.domain.entity.PromotionProducts;
import antigravity.repository.ProductRepository;
import antigravity.repository.PromotionProductsRepository;
import antigravity.repository.PromotionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;

@Configuration
public class DummyDevInit{

    @Profile("dev")
    @Bean
    CommandLineRunner init(PromotionRepository promotionRepository, ProductRepository productRepository,
                           PromotionProductsRepository promotionProductsRepository) {

        return (args) -> {
            Product productPS = productRepository.save(newProduct("panties", 100000));
            //사용가능한, Coupon 프로모션
            Promotion promotionCouponAvailablePS = promotionRepository.save(newPromotion("COUPON", "promotion1", "WON", 5000
                    , LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2)));
            //사용가능한, Code 프로모션
            Promotion promotionCodeAvailablePS = promotionRepository.save(newPromotion("CODE", "promotion2", "PERCENT", 10
                    , LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2)));
            //사용 불가능한, Coupon 프로모션
            Promotion promotionCouponDisabledPS = promotionRepository.save(newPromotion("COUPON", "promotion3", "WON", 5000
                    , LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(10)));
            //사용 불가능한, Code 프로모션
            Promotion promotionCodeDisabledPS = promotionRepository.save(newPromotion("CODE", "promotion4", "PERCENT", 10
                    , LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(10)));

            promotionProductsRepository.save(newPromotionProducts(productPS, promotionCouponAvailablePS));
            promotionProductsRepository.save(newPromotionProducts(productPS, promotionCodeAvailablePS));
            promotionProductsRepository.save(newPromotionProducts(productPS, promotionCouponDisabledPS));
            promotionProductsRepository.save(newPromotionProducts(productPS, promotionCodeDisabledPS));
        };
    }

    private PromotionProducts newPromotionProducts(Product product, Promotion promotion) {
        return PromotionProducts.builder()
                .product(product)
                .promotion(promotion)
                .build();
    }

    private Promotion newPromotion(String promotionType, String name, String discountType, int discountValue, LocalDateTime useStartedAt, LocalDateTime useEndedAt) {
        return Promotion.builder()
                .promotion_type(promotionType)
                .name(name)
                .discount_type(discountType)
                .discount_value(discountValue)
                .use_started_at(useStartedAt)
                .use_ended_at(useEndedAt)
                .build();
    }

    private Product newProduct(String name, int price) {
        return Product.builder()
                .name(name)
                .price(price)
                .build();
    }
}
