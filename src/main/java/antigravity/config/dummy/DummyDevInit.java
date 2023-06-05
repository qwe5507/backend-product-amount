package antigravity.config.dummy;

import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import antigravity.repository.ProductRepository;
import antigravity.repository.PromotionProductsRepository;
import antigravity.repository.PromotionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;

@Configuration
public class DummyDevInit extends DummyObject {

    @Profile("dev")
    @Bean
    CommandLineRunner init(PromotionRepository promotionRepository, ProductRepository productRepository,
                           PromotionProductsRepository promotionProductsRepository) {

        return (args) -> {
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
        };
    }
}
