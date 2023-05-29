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

    private Boolean isDeleted; // 삭제 상태

    private Boolean isAccept; // 접근 허용 상태

    public UserInventoryDTO.ResponseUserInventory toResponseDTO() {
        return UserInventoryDTO.ResponseUserInventory.builder()
                .userId(user.getId())
                .inventoryId(inventory.getId())
                .email(user.getEmail())
                .inventoryName(inventory.getInventoryName())
                .build();
    }

    // 인벤토리 삭제 처리 메소드
    public void deleteUserInventory() {
        this.isDeleted = false;
    }

    // 인벤토리 접근 가능 처리 메소드
    public void acceptUserInventory() {
        this.isAccept = true;
    }

}