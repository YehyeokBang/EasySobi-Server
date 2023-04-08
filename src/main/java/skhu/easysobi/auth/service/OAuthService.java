package skhu.easysobi.auth.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import skhu.easysobi.auth.dto.TokenDTO;
import skhu.easysobi.auth.dto.UserDTO;
import skhu.easysobi.auth.jwt.TokenProvider;
import skhu.easysobi.auth.repository.UserRepository;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${kakao-api-key}")
    private String KAKAO_REST_API_KEY;

    @Value("${kakao-redirect-url}")
    private String KAKAO_REDIRECT_URL;

    public TokenDTO.KakaoToken getKakaoToken (String code) {

        String accessToken = "";
        String refreshToken = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // POST 요청을 위해 기본값이 false인 setDoOutput을 true로 설정
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            String sb = "grant_type=authorization_code" +
                    "&client_id=" + KAKAO_REST_API_KEY +
                    "&redirect_uri=" + KAKAO_REDIRECT_URL +
                    "&code=" + code;
            bw.write(sb);
            bw.flush();

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            StringBuilder result = new StringBuilder();

            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            // Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonElement element = JsonParser.parseString(result.toString()).getAsJsonObject();

            accessToken = element.getAsJsonObject().get("access_token").getAsString();
            refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();

            br.close();
            bw.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new TokenDTO.KakaoToken(accessToken, refreshToken);
    }

    @Transactional
    public TokenDTO.ServiceToken joinAndLogin(String token) {

        String reqURL = "https://kapi.kakao.com/v2/user/me";
        String email = "";
        String nickname = "";
        long id = 0;

        // accessToken을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); // 전송할 header 작성, access_token전송

            // int responseCode = conn.getResponseCode(); 200이면 성공

            // 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuilder result = new StringBuilder();

            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            // Gson 라이브러리로 JSON파싱
            JsonElement element = JsonParser.parseString(result.toString()).getAsJsonObject();;

            id = element.getAsJsonObject().get("id").getAsLong();

            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            if (hasEmail) {
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            } else {
                throw new IllegalStateException("이메일을 등록하지 않았습니다");
            }

            boolean hasNickname = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("profile_nickname_needs_agreement").getAsBoolean();
            if (!hasNickname) {
                nickname = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("profile").getAsJsonObject().get("nickname").getAsString();
            } else {
                throw new IllegalStateException("닉네임을 불러올 수 없습니다");
            }

            br.close();

        } catch (IOException e) {
            throw new IllegalStateException("카카오 서버에서 유저의 정보를 불러오는 중 오류 발생");
        }

        // 카카오 로그인을 한 유저가 처음 왔다면 회원가입
        if (userRepository.findByEmail(email).isEmpty()) {
            UserDTO.RequestSignup dto = new UserDTO.RequestSignup(email, nickname, id);
            userRepository.save(dto.toEntity());
        }

        // 토큰 발급
        TokenDTO.ServiceToken tokenDTO = tokenProvider.createToken(email);

        // refreshToken의 유효기간
        Long expiration = tokenProvider.getExpiration(tokenDTO.getRefreshToken());

        // refreshToken을 redis에 저장 후 유효성 검증에 사용
        redisTemplate.opsForValue()
                .set(tokenDTO.getRefreshToken(), "refreshToken", expiration, TimeUnit.MILLISECONDS);

        return tokenDTO;
    }

    // 리프레시
    public TokenDTO.ServiceToken refresh(HttpServletRequest request, TokenDTO.ServiceToken dto) {
        String refreshToken = dto.getRefreshToken();

        String isValidate = (String)redisTemplate.opsForValue().get(refreshToken);
        if (!ObjectUtils.isEmpty(isValidate)) {
            return tokenProvider.createAccessTokenByRefreshToken(request, refreshToken);
        } else {
            throw new IllegalStateException("리프레시 토큰 만료");
        }
    }

    // 로그아웃
    public void logout(HttpServletRequest request, @RequestBody TokenDTO.ServiceToken dto) {
        // accessToken 값
        String accessToken = tokenProvider.resolveToken(request);

        // 만료 기간
        Long expiration = tokenProvider.getExpiration(accessToken);

        // 블랙 리스트 추가
        redisTemplate.opsForValue()
                .set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

        // 가지고 있던 refreshToken 제거
        redisTemplate.delete(dto.getRefreshToken());
    }

}