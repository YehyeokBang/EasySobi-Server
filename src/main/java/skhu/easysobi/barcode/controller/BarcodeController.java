package skhu.easysobi.barcode.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import skhu.easysobi.barcode.dto.BarcodeDTO;
import skhu.easysobi.barcode.service.BarcodeService;

@Tag(name = "바코드")
@RestController
@RequestMapping("/barcode")
@RequiredArgsConstructor
public class BarcodeController {

    private final BarcodeService barcodeService;

    @GetMapping("")
    @Operation(
            summary = "바코드로 식품 정보 조회",
            description = "바코드로 식품을 조회 (식품명, 유통기한 등)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)")
            })
    public BarcodeDTO.Response getFoodInfo(@RequestParam String barcode) {
        return barcodeService.getFoodInfo(barcode);
    }

}