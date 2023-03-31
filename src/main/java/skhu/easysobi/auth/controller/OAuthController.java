package skhu.easysobi.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import skhu.easysobi.auth.dto.TokenDTO;
import skhu.easysobi.auth.service.OAuthService;

@Tag(name = "인증")
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    @ResponseBody
    @GetMapping("/kakao")
    @Operation(
            summary = "카카오 토큰 발급",
            description = "지정된 URL을 통해 카카오 로그인시 카카오 토큰을 발급합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "404", description = "404")
            })
    public TokenDTO.KakaoToken kakaoCallback(@RequestParam String code) {
        return oAuthService.getKakaoToken(code);
    }

    @GetMapping("/login")
    @Operation(
            summary = "카카오 토큰으로 로그인",
            description = "존재하지 않은 유저일 경우 회원가입 진행 후 로그인합니다",
            parameters = {
                    @Parameter(name = "token", description = "카카오 엑세스 토큰", example = "7clv5lqa33gtg5")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "404", description = "404")
            })
    public TokenDTO.ServiceToken kakaoLogin(String token) {
        return oAuthService.joinAndLogin(token);
    }

}