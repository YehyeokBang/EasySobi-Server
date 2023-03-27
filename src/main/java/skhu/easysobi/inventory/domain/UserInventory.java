package skhu.easysobi.inventory.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import skhu.easysobi.auth.domain.User;
import skhu.easysobi.inventory.dto.UserInventoryDTO;

@Entity
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

    public UserInventoryDTO.Response toResponseDTO() {
        return UserInventoryDTO.Response.builder()
                .userId(user.getId())
                .InventoryId(inventory.getId())
                .build();
    }
}
