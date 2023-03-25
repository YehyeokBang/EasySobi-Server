package skhu.easysobi.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import skhu.easysobi.auth.domain.User;

public class UserDTO {

    @Data
    @Getter
    @AllArgsConstructor
    public static class RequestSignup {

        private String email;

        private String nickname;

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
