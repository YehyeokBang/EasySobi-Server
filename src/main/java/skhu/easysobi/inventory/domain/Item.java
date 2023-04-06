package skhu.easysobi.inventory.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import skhu.easysobi.common.BaseTime;
import skhu.easysobi.inventory.dto.ItemDTO;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Item extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 식품 이름

    private Long categoryNum; // 식품 카테고리 번호

    @JsonIgnore
    @ManyToOne(targetEntity = Category.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private Long count; // 식품 개수

    private LocalDateTime mfgDate; // 제조일자

    private LocalDateTime expDate; // 소비기한

    @Builder.Default
    private Boolean itemStatus = true; // 상태

    @ManyToOne(targetEntity = Inventory.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory; // N:1, 인벤토리

    public ItemDTO.ResponseItem toResponseDTO() {
        return ItemDTO.ResponseItem.builder()
                .id(id)
                .name(name)
                .categoryNum(categoryNum)
                .count(count)
                .mfgDate(mfgDate)
                .createDate(getCreateDate())
                .modifiedDate(getModifiedDate())
                .build();
    }

    // 아이템 수정 메소드
    public void updateItem(String name, Long categoryNum, Long count, LocalDateTime mfgDate, LocalDateTime expDate) {
        this.name = name;
        this.categoryNum = categoryNum;
        this.count = count;
        this.mfgDate = mfgDate;
        this.expDate = expDate;
    }

    // 아이템 삭제 처리 메소드
    public void deleteItem() {
        this.itemStatus = false;
    }

}