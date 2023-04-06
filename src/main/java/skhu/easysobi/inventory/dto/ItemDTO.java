package skhu.easysobi.inventory.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import skhu.easysobi.inventory.domain.Inventory;
import skhu.easysobi.inventory.domain.Item;

import java.time.LocalDateTime;

public class ItemDTO {

    @Data
    @Builder
    @Schema(description = "아이템 응답 DTO")
    public static class ResponseItem {

        @Schema(description = "아이템 id")
        private Long id;

        @Schema(description = "아이템 이름")
        private String name;

        @Schema(description = "아이템 카테고리 번호")
        private Long categoryNum;

        @Schema(description = "아이템 개수")
        private Long count;

        @Schema(description = "아이템 제조 일자")
        private LocalDateTime mfgDate;

        @Schema(description = "아이템 소비 기한")
        private LocalDateTime expDate;

        @Schema(description = "아이템 최초 등록 일자")
        private LocalDateTime createDate;

        @Schema(description = "아이템 최근 수정 일자")
        private LocalDateTime modifiedDate;

    }

    @Data
    @Schema(description = "아이템 생성 요청 DTO")
    public static class RequestCreateItem {

        @Schema(description = "아이템 이름")
        private String name;

        @Schema(description = "아이템 카테고리 번호")
        private Long categoryNum;

        @Schema(description = "아이템 개수")
        private Long count;

        @Schema(description = "아이템 제조 일자")
        private LocalDateTime mfgDate;

        @Schema(description = "아이템 소비 기한 직접 작성하지 마세요")
        private LocalDateTime expDate;

        @Schema(description = "인벤토리 id")
        private Long inventoryId;

        @JsonIgnore
        private Inventory inventory;

        public Item toEntity() {
            return Item.builder()
                    .name(name)
                    .categoryNum(categoryNum)
                    .count(count)
                    .mfgDate(mfgDate)
                    .expDate(expDate)
                    .inventory(inventory)
                    .build();
        }

    }

}