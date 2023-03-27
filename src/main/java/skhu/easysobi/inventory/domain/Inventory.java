package skhu.easysobi.inventory.domain;

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
@Setter
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

    @OneToMany(mappedBy = "inventory")
    private List<UserInventory> userInventoryList = new ArrayList<>();

    public InventoryDTO.Response toResponseDTO() {
        return InventoryDTO.Response.builder()
                .inventoryName(inventoryName)
                .itemList(itemList.stream()
                        .map(Item::toDTO).collect(Collectors.toList()))
                .build();
    }

}
