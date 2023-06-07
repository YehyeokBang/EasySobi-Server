package skhu.easysobi.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skhu.easysobi.auth.dto.TokenDTO;
import skhu.easysobi.auth.dto.UserDTO;
import skhu.easysobi.auth.service.OAuthService;

import java.security.Principal;

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
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public TokenDTO.KakaoToken kakaoCallback(@RequestParam String code) {
        return oAuthService.getKakaoToken(code);
    }

    @PostMapping("/login")
    @Operation(
            summary = "카카오 토큰으로 로그인",
            description = "존재하지 않은 유저일 경우 회원가입 진행 후 로그인합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "400", description = "카카오 서버에 유저의 이메일이 없거나, 닉네임이 없음"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)")
            })
    public ResponseEntity<TokenDTO.ServiceToken> login(@RequestBody UserDTO.RequestLogin dto) {
        TokenDTO.ServiceToken serviceToken = oAuthService.joinAndLogin(dto);
        return ResponseEntity.ok(serviceToken);
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "리프레시",
            description = "리프레시 토큰을 통해 엑세스 토큰 유효 기간 초기화",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "400", description = "리프레시 토큰 만료"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)")
            })
    public ResponseEntity<TokenDTO.ServiceToken> refresh(HttpServletRequest request, @RequestBody TokenDTO.ServiceToken dto) {
        TokenDTO.ServiceToken serviceToken = oAuthService.refresh(request, dto);
        return ResponseEntity.ok(serviceToken);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "액세스 토큰 블랙리스트에 저장 및 리프레시 토큰 제거",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<String> logout(HttpServletRequest request, @RequestBody TokenDTO.ServiceToken dto, Principal principal) {
        oAuthService.logout(request, dto, principal);
        return ResponseEntity.ok("로그아웃 완료");
    }

}