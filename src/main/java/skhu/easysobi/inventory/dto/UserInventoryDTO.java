package skhu.easysobi.inventory.dto;

import lombok.*;
import skhu.easysobi.auth.domain.User;
import skhu.easysobi.inventory.domain.Inventory;
import skhu.easysobi.inventory.domain.UserInventory;

public class UserInventoryDTO {

    @Data
    @Getter
    @Builder
    public static class Response {

        private Long userId;

        private Long InventoryId;

    }

    @Data
    @Getter
    @Builder
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class RequestCreate {

        private User user;

        private Inventory inventory;

        public UserInventory toEntity() {
            return UserInventory.builder()
                    .user(user)
                    .inventory(inventory)
                    .build();
        }

    }

}
