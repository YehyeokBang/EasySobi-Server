package skhu.easysobi.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

public class CategoryDTO {

    @Data
    @Schema(description = "카테고리 생성 요청 DTO")
    public static class RequestCreateCategory {

        @Schema(description = "카테고리 번호")
        private Long categoryNum;

        @Schema(description = "카테고리 이름")
        private String categoryName;

        @Schema(description = "소비기한 일수")
        private int exp;

    }

    @Data
    @Schema(description = "카테고리 응답 DTO")
    public static class ResponseCategory {

        @Schema(description = "카테고리 번호")
        private Long categoryNum;

        @Schema(description = "카테고리 이름")
        private String categoryName;

        @Schema(description = "소비기한 일수")
        private int exp;

    }

}
