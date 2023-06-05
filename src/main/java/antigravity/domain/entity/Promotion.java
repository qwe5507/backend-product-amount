package antigravity.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 6)
    private String promotion_type; //쿠폰 타입 (쿠폰, 코드)

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 6)
    private String discount_type; // WON : 금액 할인, PERCENT : %할인

    @Column(nullable = false)
    private int discount_value; // 할인 금액 or 할인 %

    @Column(nullable = false)
    private LocalDateTime use_started_at; // 쿠폰 사용가능 시작 기간

    @Column(nullable = false)
    private LocalDateTime use_ended_at; // 쿠폰 사용가능 종료 기간

    @Builder
    public Promotion(Integer id, String promotion_type, String name, String discount_type, int discount_value, LocalDateTime use_started_at, LocalDateTime use_ended_at) {
        this.id = id;
        this.promotion_type = promotion_type;
        this.name = name;
        this.discount_type = discount_type;
        this.discount_value = discount_value;
        this.use_started_at = use_started_at;
        this.use_ended_at = use_ended_at;
    }
}
