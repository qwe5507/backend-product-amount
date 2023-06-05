package antigravity.repository;

import antigravity.domain.entity.PromotionProducts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionProductsRepository extends JpaRepository<PromotionProducts, Integer> {
}
