package skhu.easysobi.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class TokenDTO {

    @Data
    @Builder
    @AllArgsConstructor
    public static class KakaoToken {

        private String kakaoAccessToken;

        private String kakaoRefreshToken;

    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class ServiceToken {

        private String accessToken;

        private String refreshToken;

    }

}
