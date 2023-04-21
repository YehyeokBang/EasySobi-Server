package skhu.easysobi.barcode.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import skhu.easysobi.barcode.dto.BarcodeDTO;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class BarcodeService {

    @Value("${barcode-api-key}")
    private String apiKey;

    public BarcodeDTO.Response getFoodInfo (String barcode) {
        // 식품명, 식품 유형, 유통/소비기한 정보
        String name = "";
        String type = "";
        String expInfo = "";

        String reqURL = "http://openapi.foodsafetykorea.go.kr/api/" + apiKey + "/C005/json/1/5/BAR_CD=" + barcode;

        try {
            URL url = new URL(reqURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            StringBuilder result = new StringBuilder();

            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            // Gson 라이브러리로 JSON파싱
            JsonElement element = JsonParser.parseString(result.toString()).getAsJsonObject();
            JsonObject jsonObject = element.getAsJsonObject().get("C005").getAsJsonObject().get("row").getAsJsonArray().asList().get(0).getAsJsonObject();;
            name = jsonObject.get("PRDLST_NM").getAsString();
            type = jsonObject.get("POG_DAYCNT").getAsString();
            expInfo = jsonObject.get("PRDLST_DCNM").getAsString();

            br.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new BarcodeDTO.Response(name, type, expInfo, barcode);
    }
}