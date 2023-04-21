package skhu.easysobi.barcode.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

public class BarcodeDTO {

    @Data
    @AllArgsConstructor
    @Schema(description = "바코드 조회 결과 DTO")
    public static class Response {

        @Schema(description = "식품명")
        private String name;

        @Schema(description = "식품 유형")
        private String type;

        @Schema(description = "유통/소비기한")
        private String expInfo;

        @Schema(description = "바코드 번호")
        private String barcode;

    }

}
