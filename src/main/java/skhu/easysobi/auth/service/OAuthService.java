package skhu.easysobi.auth.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import skhu.easysobi.auth.domain.User;
import skhu.easysobi.auth.dto.TokenDTO;
import skhu.easysobi.auth.dto.UserDTO;
import skhu.easysobi.auth.jwt.TokenProvider;
import skhu.easysobi.auth.repository.UserRepository;
import skhu.easysobi.common.exception.CustomException;
import skhu.easysobi.push.repository.PushRepository;

import java.security.Principal;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static skhu.easysobi.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PushRepository pushRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${kakao-api-key}")
    private String KAKAO_REST_API_KEY;

    @Value("${kakao-redirect-url}")
    private String KAKAO_REDIRECT_URL;

    public TokenDTO.KakaoToken getKakaoToken (String code) {
        // RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();
        // 카카오 API 요청 URL 설정
        String reqURL = "https://kauth.kakao.com/oauth/token";

        // POST 요청을 위한 파라미터 MultiValueMap에 추가
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("grant_type", "authorization_code");
        requestParams.add("client_id", KAKAO_REST_API_KEY);
        requestParams.add("redirect_uri", KAKAO_REDIRECT_URL);
        requestParams.add("code", code);

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 요청 엔티티 객체 생성 (파라미터, 헤더 포함)
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestParams, headers);

        // RestTemplate을 이용해 요청을 수행하고 응답을 받음
        ResponseEntity<String> responseEntity = restTemplate.exchange(reqURL, HttpMethod.POST, requestEntity, String.class);

        // 성공적인 응답을 받지 못한 경우 예외 발생
        if (!responseEntity.getStatusCode().is2xxSuccessful()) throw new CustomException(INVALID_KAKAO_VALUE);

        // 응답 본문(JSON)을 파싱하기 위한 JsonElement 객체 생성
        JsonElement element = JsonParser.parseString(Objects.requireNonNull(responseEntity.getBody())).getAsJsonObject();

        // JsonElement 객체에서 access_token, refresh_token 추출
        String accessToken = element.getAsJsonObject().get("access_token").getAsString();
        String refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();

        // 추출한 토큰 값들로 TokenDTO.KakaoToken 객체 생성 후 반환
        return new TokenDTO.KakaoToken(accessToken, refreshToken);
    }

    @Transactional
    public TokenDTO.ServiceToken joinAndLogin(UserDTO.RequestLogin dto) {
        // RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();
        // 카카오 API 요청 URL 설정
        String reqURL = "https://kapi.kakao.com/v2/user/me";

        // 헤더 설정, accessToken 전송
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.set("Authorization", "Bearer " + dto.getKakaoToken());

        // HttpEntity 생성, 헤더 포함
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // RestTemplate을 이용해 요청을 수행하고 응답을 받음
        ResponseEntity<String> responseEntity = restTemplate.exchange(reqURL, HttpMethod.POST, requestEntity, String.class);

        // 이메일, 닉네임, 카카오 ID를 담을 변수 선언
        String email, nickname = "";
        long id = 0;

        // 성공적인 응답인 경우
        if (!responseEntity.getStatusCode().is2xxSuccessful()) throw new CustomException(INVALID_KAKAO_VALUE);

        // 응답 본문(JSON)을 파싱하기 위한 JsonElement 객체 생성
        JsonElement element = JsonParser.parseString(Objects.requireNonNull(responseEntity.getBody())).getAsJsonObject();

        // JsonElement 객체에서 id, email, nickname 추출
        id = element.getAsJsonObject().get("id").getAsLong();

        // 이메일 추출, 카카오 계정 내에 이메일을 가지고 있지 않은 경우 예외 발생
        boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
        if (!hasEmail) throw new CustomException(EMAIL_NOT_FOUND);
        email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();

        // 닉네임 추출, 카카오 계정 닉네임 제공 동의를 하지 않은 경우 예외 발생
        // (nicknameNeedsAgreement == true)이면 닉네임 제공 동의를 한 것
        boolean nicknameNeedsAgreement = !element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("profile_nickname_needs_agreement").getAsBoolean();
        if (!nicknameNeedsAgreement) throw new CustomException(NAME_NOT_FOUND);
        nickname = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("profile").getAsJsonObject().get("nickname").getAsString();

        // 카카오 로그인을 한 유저가 처음 왔다면 회원가입
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User(email, nickname, id);
            userRepository.save(user);
        }

        // FCM 토큰 저장
        pushRepository.saveToken(email, dto.getFcmToken());

        // 토큰 발급
        TokenDTO.ServiceToken tokenDTO = tokenProvider.createToken(email);

        // refreshToken의 유효기간
        Long expiration = tokenProvider.getExpiration(tokenDTO.getRefreshToken());

        // refreshToken을 redis에 저장 후 유효성 검증에 사용
        redisTemplate.opsForValue().set(tokenDTO.getRefreshToken(), "refreshToken", expiration, TimeUnit.MILLISECONDS);

        return tokenDTO;
    }


    // 리프레시
    public TokenDTO.ServiceToken refresh(HttpServletRequest request, TokenDTO.ServiceToken dto) {
        String refreshToken = dto.getRefreshToken();

        // refreshToken이 유효하지 않은 경우 예외 발생
        String isValidate = (String)redisTemplate.opsForValue().get(refreshToken);
        if (ObjectUtils.isEmpty(isValidate)) throw new CustomException(INVALID_REFRESH_TOKEN);

        // AccessToken 재발급
        return tokenProvider.createAccessTokenByRefreshToken(request, refreshToken);
    }

    // 로그아웃
    public void logout(HttpServletRequest request, @RequestBody TokenDTO.ServiceToken dto, Principal principal) {
        // accessToken 값
        String accessToken = tokenProvider.resolveToken(request);

        // 만료 기간
        Long expiration = tokenProvider.getExpiration(accessToken);

        // 블랙 리스트 추가
        redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

        // 가지고 있던 refreshToken 제거
        redisTemplate.delete(dto.getRefreshToken());

        // FCM 토큰 제거
        pushRepository.deleteToken(principal.getName());
    }

}