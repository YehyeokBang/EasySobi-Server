package skhu.easysobi.inventory.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private Long categoryNum; // 카테고리 번호

    private String categoryName; // 카테고리 이름

    private int exp;

}
