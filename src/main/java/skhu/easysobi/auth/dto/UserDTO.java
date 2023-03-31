package skhu.easysobi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import skhu.easysobi.auth.domain.User;

public class UserDTO {

    @Data
    @Getter
    @AllArgsConstructor
    @Schema(description = "회원가입 요청 DTO")
    public static class RequestSignup {

        @Schema(description = "이메일")
        private String email;

        @Schema(description = "이름")
        private String nickname;

        @Schema(description = "카카오 id")
        private Long kakaoId;

        @Builder
        public User toEntity() {
            return User.builder()
                    .email(email)
                    .nickname(nickname)
                    .kakaoId(kakaoId)
                    .build();
        }

    }

}
