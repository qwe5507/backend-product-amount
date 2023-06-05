package antigravity.repository;

import antigravity.domain.entity.Product;
import antigravity.domain.entity.PromotionProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PromotionProductsRepository extends JpaRepository<PromotionProducts, Integer> {

    @Query("SELECT ps FROM PromotionProducts ps JOIN FETCH ps.promotion p WHERE ps.product = :product AND p.id IN :ids")
    List<PromotionProducts> findByProductAndPromotionIds(@Param("product") Product product, @Param("ids") List<Integer> ids);
}
