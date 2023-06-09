package skhu.easysobi.inventory.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import skhu.easysobi.common.BaseTime;
import skhu.easysobi.inventory.dto.InventoryDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Builder
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Inventory extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    private String inventoryName; // 인벤토리 이름

    @OneToMany(mappedBy = "inventory")
    private List<Item> itemList; // 식품 목록

    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "inventory")
    private List<UserInventory> userInventoryList = new ArrayList<>();

    @Builder.Default
    private Boolean isDeleted = false; // 삭제 여부

    public InventoryDTO.ResponseInventory toResponseDTO() {
        return InventoryDTO.ResponseInventory.builder()
                .inventoryId(id)
                .inventoryName(inventoryName)
                .itemList(itemList.stream()
                        .map(Item::toResponseDTO).collect(Collectors.toList()))
                .build();
    }

    public InventoryDTO.ResponseMiniInventory toResponseMiniInventoryDTO() {
        return InventoryDTO.ResponseMiniInventory.builder()
                .inventoryId(id)
                .inventoryName(inventoryName)
                .build();
    }

    // 인벤토리 이름 업데이트 메소드
    public void updateInventoryName(String inventoryName) {
        this.inventoryName = inventoryName;
    }

    // 인벤토리 아이템 목록 업데이트 메소드
    public void updateInventoryItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    // 인벤토리 삭제 처리 메소드
    public void deleteInventory() {
        this.isDeleted = true;
    }

}