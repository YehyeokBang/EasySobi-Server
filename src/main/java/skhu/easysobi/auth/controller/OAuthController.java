package skhu.easysobi.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import skhu.easysobi.auth.dto.TokenDTO;
import skhu.easysobi.auth.service.OAuthService;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    @ResponseBody
    @GetMapping("/kakao")
    public TokenDTO.KakaoToken kakaoCallback(@RequestParam String code) {
        return oAuthService.getKakaoToken(code);
    }

    @GetMapping("/login")
    public TokenDTO.ServiceToken kakaoLogin(String token) {
        return oAuthService.joinAndLogin(token);
    }


}