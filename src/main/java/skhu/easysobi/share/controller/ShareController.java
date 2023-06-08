package skhu.easysobi.share.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skhu.easysobi.inventory.dto.UserInventoryDTO;
import skhu.easysobi.share.dto.ShareDTO;
import skhu.easysobi.share.service.ShareService;

import java.security.Principal;
import java.util.List;

@Tag(name = "공유")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/share")
public class ShareController {

    private final ShareService shareService;

    @PostMapping("")
    @Operation(
            summary = "인벤토리 공유 요청",
            description = "원하는 유저에게 자신의 인벤토리를 공유하기",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)")
            })
    public ResponseEntity<String> shareInventory(@RequestBody ShareDTO.RequestShare dto) {
        shareService.shareInventory(dto);
        return ResponseEntity.ok("완료");
    }

    @GetMapping("list")
    @Operation(
            summary = "다른 사용자가 공유한 인벤토리 목록 (수락 전)",
            description = "다른 사용자가 공유한 인벤토리 목록",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)")
            })
    public List<UserInventoryDTO.ResponseUserInventory> shareList(Principal principal) {
        return shareService.shareList(principal);
    }

    @PostMapping("{user_inventory_id}")
    @Operation(
            summary = "다른 사용자가 공유한 인벤토리 수락",
            description = "다른 사용자가 공유한 인벤토리 수락",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)")
            })
    public ResponseEntity<String> shareList(@PathVariable("user_inventory_id") Long id, Principal principal) {
        shareService.acceptShare(id, principal);
        return ResponseEntity.ok("완료");
    }

}