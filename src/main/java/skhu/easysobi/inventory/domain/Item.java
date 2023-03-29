package skhu.easysobi.inventory.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import skhu.easysobi.common.BaseTime;
import skhu.easysobi.inventory.dto.ItemDTO;

import java.time.LocalDateTime;

@Entity
@Builder
@Setter
@RequiredArgsConstructor
public class Item extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 식품 이름

    private Long category; // 식품 카테고리

    private Long count; // 식품 개수

    private LocalDateTime mfgDate; // 제조일자

    @Builder.Default
    private Boolean itemStatus = true; // 상태

    @ManyToOne(targetEntity = Inventory.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory; // N:1, 인벤토리

    public ItemDTO.Response toResponseDTO() {
        return ItemDTO.Response.builder()
                .id(id)
                .name(name)
                .category(category)
                .count(count)
                .mfgDate(mfgDate)
                .createDate(getCreateDate())
                .modifiedDate(getModifiedDate())
                .build();
    }

}
