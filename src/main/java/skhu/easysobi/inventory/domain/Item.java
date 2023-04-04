package skhu.easysobi.inventory.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import skhu.easysobi.common.BaseTime;
import skhu.easysobi.inventory.dto.ItemDTO;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Item extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "식품 이름은 꼭 필요해요")
    private String name; // 식품 이름

    @NotBlank(message = "카테고리는 꼭 필요해요")
    private Long category; // 식품 카테고리

    @NotBlank(message = "식품 개수는 꼭 필요해요")
    private Long count; // 식품 개수

    @NotBlank(message = "제조일자는 꼭 필요해요")
    private LocalDateTime mfgDate; // 제조일자

    @Builder.Default
    private Boolean itemStatus = true; // 상태

    @ManyToOne(targetEntity = Inventory.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory; // N:1, 인벤토리

    public ItemDTO.ResponseItem toResponseDTO() {
        return ItemDTO.ResponseItem.builder()
                .id(id)
                .name(name)
                .category(category)
                .count(count)
                .mfgDate(mfgDate)
                .createDate(getCreateDate())
                .modifiedDate(getModifiedDate())
                .build();
    }

    // 아이템 수정 메소드
    public void updateItem(String name, Long category, Long count, LocalDateTime mfgDate) {
        this.name = name;
        this.category = category;
        this.count = count;
        this.mfgDate = mfgDate;
    }

    // 아이템 삭제 처리 메소드
    public void deleteItem() {
        this.itemStatus = false;
    }

}