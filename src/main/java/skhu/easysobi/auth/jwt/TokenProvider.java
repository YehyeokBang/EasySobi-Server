package skhu.easysobi.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import skhu.easysobi.auth.dto.TokenDTO;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {

    private final Key key;
    private final long accessTokenValidityTime;
    private final long refreshTokenValidityTime;

    public TokenProvider(@Value("${jwt.secret}") String secretKey,
                         @Value("${jwt.access-token-validity-in-milliseconds}") long accessTokenValidityTime,
                         @Value("${jwt.refresh-token-validity-in-milliseconds}") long refreshTokenValidityTime) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityTime = accessTokenValidityTime;
        this.refreshTokenValidityTime = refreshTokenValidityTime;
    }

    // 로그인 후 초기 토큰 생성
    public TokenDTO.ServiceToken createToken(String email) {

        // 현재 시간
        long now = (new Date()).getTime();

        // AccessToken 유효 기간
        Date tokenExpiredTime = new Date(now + accessTokenValidityTime);

        // AccessToken 생성
        String accessToken = Jwts.builder()
                .setSubject(email)
                .claim("auth", "ROLE_USER")
                .setExpiration(tokenExpiredTime)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // RefreshToken 유효 기간
        tokenExpiredTime = new Date(now + refreshTokenValidityTime);

        // RefreshToken 생성
        String refreshToken = Jwts.builder()
                .setExpiration(tokenExpiredTime)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // TokenDTO 형태로 반환
        return TokenDTO.ServiceToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // refreshToken을 이용한 생명 연장
    public TokenDTO.ServiceToken createAccessTokenByRefreshToken(HttpServletRequest request, String refreshToken) {
        String[] chunks = resolveToken(request).split("\\.");

        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        String name = payload.split("\"")[3];

        // 현재 시간
        long now = (new Date()).getTime();

        // AccessToken 유효 기간
        Date tokenExpiredTime = new Date(now + accessTokenValidityTime);

        // AccessToken 생성
        String accessToken = Jwts.builder()
                .setSubject(name)
                .claim("auth", "ROLE_USER")
                .setExpiration(tokenExpiredTime)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // TokenDTO 형태로 반환
        return TokenDTO.ServiceToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 토큰에 담겨있는 정보를 가져오는 메소드
    public Authentication getAuthentication(String accessToken) {
        // AccessToken <- Claims 추출
        Claims claims = parseClaims(accessToken);

        // 권한 정보가 담겨있지 않은 토큰을 받았을 경우
        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 위 과정을 통과하면 권한 정보가 있는 토큰임

        // claims <- 권한 정보 추출
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 생성해서 UsernamePasswordAuthenticationToken 형태로 반환 -> SecurityContext 사용을 위함
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    //  Request Header에서 토큰 값을 가져오는 메소드, "Authorization": "토큰 값"
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // 가져온 값이 비어있지 않으면서 "Bearer "로 시작한다면
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // "Bearer ~값~" 형식인데, "~값~"만 가져와서 반환
            return bearerToken.substring(7);
        }
        // 아니면 null 리턴
        return null;
    }

    // 넘어온 토큰의 유효성을 판별하는 메소드
    public boolean validateToken(String token) {
        // 매개변수로 받아온 토큰이 유효하면 true 반환
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }
        // 토큰이 유효하지 않으면 false 반환
        // MalformedJwtException: JWT가 올바르게 구성되지 않았을 때
        catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) { // accessToken 만료된 경우 refreshToken 검증
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    // 토큰 복호화 후 정보 반환
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) { // 기한 만료된 토큰
            return e.getClaims();
        }
    }

    // 남은 유효기간 반환
    public Long getExpiration(String accessToken) {
        // accessToken 남은 유효시간
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }
}
