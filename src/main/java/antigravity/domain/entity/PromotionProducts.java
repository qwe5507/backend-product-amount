package antigravity.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class PromotionProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Promotion promotion;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Builder
    public PromotionProducts(Integer id, Promotion promotion, Product product) {
        this.id = id;
        this.promotion = promotion;
        this.product = product;
    }
}
