package skhu.easysobi.share.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

public class ShareDTO {

    @Data
    @Schema(description = "인벤토리 공유 요청 DTO")
    public static class RequestShare {

        @Schema(description = "공유받을 유저의 이메일")
        private String email;

        @Schema(description = "공유할 인벤토리 id")
        private Long inventoryId;

    }

}