package skhu.easysobi.inventory.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public class ItemDTO {

    @Data
    @Builder
    public static class Response {

        private String name; // 식품 이름

        private Long category; // 식품 카테고리

        private Long count; // 식품 개수

        private LocalDateTime mfgDate; // 제조일자

        private LocalDateTime createDate; // 식품 최초 작성 일자

        private LocalDateTime modifiedDate; // 식품 정보 수정 일자

    }

}
