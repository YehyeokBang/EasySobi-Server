package skhu.easysobi.barcode.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import skhu.easysobi.barcode.dto.BarcodeDTO;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BarcodeService {

    @Value("${barcode-api-key}")
    private String apiKey;

    public BarcodeDTO.Response getFoodInfo(String barcode) {
        // RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();
        // 바코드 API 요청 URL 설정
        String reqURL = "http://openapi.foodsafetykorea.go.kr/api/" + apiKey + "/C005/json/1/5/BAR_CD=" + barcode;

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpEntity 생성, 헤더 포함
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // RestTemplate을 이용해 GET 요청 후 응답 받기
        ResponseEntity<String> responseEntity = restTemplate.exchange(reqURL, HttpMethod.GET, requestEntity, String.class);

        // 식품명, 식품 유형, 소비기한 정보를 저장할 변수 선언
        String name, type, expInfo = "";

        // 성공적인 응답인 경우
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            // 응답 본문(JSON)을 파싱하기 위한 JsonElement 객체 생성
            JsonElement element = JsonParser.parseString(Objects.requireNonNull(responseEntity.getBody())).getAsJsonObject();

            // JsonElement 객체에서 식품명, 식품 유형, 소비기한 정보 추출
            JsonObject jsonObject = element.getAsJsonObject().get("C005").getAsJsonObject().get("row").getAsJsonArray().asList().get(0).getAsJsonObject();
            name = jsonObject.get("PRDLST_NM").getAsString();
            type = jsonObject.get("PRDLST_DCNM").getAsString();
            expInfo = jsonObject.get("POG_DAYCNT").getAsString();
        } else {
            // 성공적인 응답이 아닐 경우 예외 발생
            throw new IllegalStateException("식품 정보를 불러오는 중 오류 발생: " + responseEntity.getStatusCode());
        }

        return new BarcodeDTO.Response(name, type, expInfo, barcode);
    }
}