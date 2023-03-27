package skhu.easysobi.inventory.dto;

import lombok.*;
import skhu.easysobi.inventory.domain.Inventory;

import java.util.List;

public class InventoryDTO {

    @Data
    @Builder
    @Getter
    public static class Response {

        private String inventoryName;

        private List<ItemDTO.Response> itemList;

    }

    @Data
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestCreate {

        private String inventoryName;

        public Inventory toEntity() {
            return Inventory.builder()
                    .inventoryName(inventoryName)
                    .build();
        }

    }

}
