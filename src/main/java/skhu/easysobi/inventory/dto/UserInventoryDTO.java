package skhu.easysobi.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import skhu.easysobi.auth.domain.User;
import skhu.easysobi.inventory.domain.Inventory;
import skhu.easysobi.inventory.domain.UserInventory;

public class UserInventoryDTO {

    @Data
    @Getter
    @Builder
    @Schema(description = "인벤토리 권한 응답 DTO")
    public static class ResponseUserInventory {

        @Schema(description = "유저 id")
        private Long userId;

        @Schema(description = "인벤토리 id")
        private Long InventoryId;

    }

    @Data
    @Getter
    @Builder
    @RequiredArgsConstructor
    @AllArgsConstructor
    @Schema(description = "인벤토리 권한 생성 요청 DTO")
    public static class RequestCreateUserInventory {

        @Schema(description = "유저")
        private User user;

        @Schema(description = "인벤토리")
        private Inventory inventory;

        @Schema(description = "접근 권한")
        private boolean accessStatus;

        public UserInventory toEntity() {
            return UserInventory.builder()
                    .user(user)
                    .inventory(inventory)
                    .accessStatus(accessStatus)
                    .build();
        }

    }

}