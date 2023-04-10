package skhu.easysobi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;

public class UserDTO {

    @Data
    @Getter
    @Schema(description = "회원가입 및 로그인 요청 DTO")
    public static class RequestLogin {

        @Schema(description = "카카오 로그인 시 발급받는 토큰")
        private String token;

    }

}
