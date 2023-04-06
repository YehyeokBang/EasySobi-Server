package skhu.easysobi.inventory.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skhu.easysobi.auth.domain.User;
import skhu.easysobi.inventory.dto.UserInventoryDTO;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @Builder.Default
    private Boolean accessStatus = true; // 접근 상태

    public UserInventoryDTO.ResponseUserInventory toResponseDTO() {
        return UserInventoryDTO.ResponseUserInventory.builder()
                .userId(user.getId())
                .InventoryId(inventory.getId())
                .build();
    }

    // 인벤토리 접근 불가 처리 메소드
    public void deleteUserInventory() {
        this.accessStatus = false;
    }

}