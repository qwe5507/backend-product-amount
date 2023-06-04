package antigravity.domain.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class PromotionProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Promotion promotion;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;
}
