package skhu.easysobi.inventory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skhu.easysobi.inventory.dto.InventoryDTO;
import skhu.easysobi.inventory.dto.UserInventoryDTO;
import skhu.easysobi.inventory.service.InventoryService;

import java.security.Principal;
import java.util.List;

@Tag(name = "인벤토리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/")
    @Operation(
            summary = "접근가능한 인벤토리 조회",
            description = "로그인된 유저의 정보를 이용해서 접근가능한 인벤토리를 조회합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "404", description = "404")
            })
    public List<UserInventoryDTO.ResponseUserInventory> findInventoryList(Principal principal) {
        return inventoryService.findInventoryList(principal);
    }

    @GetMapping("/{inventory_id}")
    @Operation(
            summary = "인벤토리 하나 조회",
            description = "ID를 이용해서 인벤토리 하나를 조회합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "404", description = "404")
            })
    public InventoryDTO.ResponseInventory findInventoryById(@PathVariable("inventory_id") Long id) {
        return inventoryService.findInventoryById(id);
    }

    @PostMapping("/create")
    @Operation(
            summary = "인벤토리 하나 생성",
            description = "유저의 인벤토리 하나를 생성합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "404", description = "404")
            })
    private ResponseEntity<String> createInventory(@RequestBody InventoryDTO.RequestCreateInventory dto, Principal principal) {
        try {
            inventoryService.createInventory(dto, principal);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("생성 완료");
    }

    @PutMapping("{inventory_id}")
    @Operation(
            summary = "인벤토리 수정",
            description = "ID를 이용해서 인벤토리 정보 수정합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "404", description = "404")
            })
    public void updateInventoryById(@PathVariable("inventory_id") Long id, @RequestBody InventoryDTO.RequestCreateInventory dto) {
        inventoryService.updateInventoryById(id, dto);
    }

    @PatchMapping("{inventory_id}")
    @Operation(
            summary = "인벤토리 삭제 처리",
            description = "ID를 이용해서 인벤토리 하나를 삭제 처리합니다 (status = false)",
            parameters = {
                    @Parameter(name = "id", description = "인벤토리 ID", example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "404", description = "404")
            })
    public void deleteInventoryById(@PathVariable("inventory_id") Long id) {
        inventoryService.deleteInventorById(id);
    }


}