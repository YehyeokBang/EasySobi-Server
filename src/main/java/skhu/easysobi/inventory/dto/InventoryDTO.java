package skhu.easysobi.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import skhu.easysobi.inventory.domain.Inventory;

import java.util.List;

public class InventoryDTO {

    @Data
    @Builder
    @Getter
    @Schema(description = "인벤토리 정보 응답 DTO")
    public static class ResponseInventory {

        @Schema(description = "인벤토리 이름")
        private String inventoryName;

        @Schema(description = "인벤토리 내 아이템 목록")
        private List<ItemDTO.ResponseItem> itemList;

    }

    @Data
    @Builder
    @Getter
    @AllArgsConstructor
    @Schema(description = "메인 페이지 내 인벤토리 응답 DTO")
    public static class ResponseMiniInventory {

        @Schema(description = "인벤토리 이름")
        private String inventoryName;

        @Schema(description = "인벤토리 내 아이템 개수")
        private Integer itemCount;

        @Schema(description = "인벤토리 내 기한 만료 위험 아이템 목록")
        private List<ItemDTO.ResponseItem> imminentItemList;

    }

    @Data
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "인벤토리 생성 요청 DTO")
    public static class RequestCreateInventory {

        @Schema(description = "사용할 인벤토리 이름")
        private String inventoryName;

        public Inventory toEntity() {
            return Inventory.builder()
                    .inventoryName(inventoryName)
                    .build();
        }

    }

}