package antigravity.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@NoArgsConstructor
@Getter
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private int price;

    @Builder
    public Product(Integer id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}
