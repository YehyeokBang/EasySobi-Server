package skhu.easysobi.inventory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skhu.easysobi.inventory.dto.InventoryDTO;
import skhu.easysobi.inventory.service.InventoryService;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Tag(name = "인벤토리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("")
    @Operation(
            summary = "메인 페이지",
            description = "로그인된 유저의 정보를 이용해서 간이 인벤토리 정보를 조회합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)")
            })
    public List<InventoryDTO.ResponseMiniInventory> findInventoryList(Principal principal) {
        return inventoryService.mainPage(principal);
    }

    @GetMapping("/{inventory_id}")
    @Operation(
            summary = "인벤토리 하나 조회",
            description = "ID를 이용해서 인벤토리 하나를 조회합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "400", description = "인벤토리 id 확인"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)")
            })
    public ResponseEntity<Object> findInventoryById(@PathVariable("inventory_id") Long id) {
        try {
            InventoryDTO.ResponseInventory inventory = inventoryService.findInventoryById(id);
            return ResponseEntity.ok(inventory);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    @Operation(
            summary = "인벤토리 하나 생성",
            description = "유저의 인벤토리 하나를 생성합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "400", description = "유저 정보 확인"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)")
            })
    private ResponseEntity<String> createInventory(@RequestBody InventoryDTO.RequestInventory dto,
                                                   Principal principal) {
        try {
            Long inventoryId = inventoryService.createInventory(dto, principal);
            return ResponseEntity.ok("인벤토리 생성 완료 id: " + inventoryId);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{inventory_id}")
    @Operation(
            summary = "인벤토리 수정",
            description = "ID를 이용해서 인벤토리 정보 수정합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "400", description = "인벤토리 id 확인"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)")
            })
    public ResponseEntity<String> updateInventoryById(@PathVariable("inventory_id") Long id,
                                                      @RequestBody InventoryDTO.RequestInventory dto) {
        try {
            Long inventoryId = inventoryService.updateInventoryById(id, dto);
            return ResponseEntity.ok("인벤토리 수정 완료 id: " + inventoryId);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("{inventory_id}")
    @Operation(
            summary = "인벤토리 삭제 처리",
            description = "ID를 이용해서 인벤토리 하나를 삭제 처리합니다 (status = false)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "400", description = "인벤토리 id 확인"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)")
            })
    public ResponseEntity<String> deleteInventoryById(@PathVariable("inventory_id") Long id, Principal principal) {
        try {
            Long inventoryId = inventoryService.deleteInventorById(id, principal);
            return ResponseEntity.ok("인벤토리 삭제 처리 완료 id: " + inventoryId);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}