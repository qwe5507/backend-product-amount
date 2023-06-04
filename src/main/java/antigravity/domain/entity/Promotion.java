package antigravity.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
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
}
