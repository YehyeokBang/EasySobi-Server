package skhu.easysobi.inventory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skhu.easysobi.inventory.dto.ItemDTO;
import skhu.easysobi.inventory.service.ItemService;

@Tag(name = "아이템")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/item")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{item_id}")
    @Operation(
            summary = "아이템 하나 조회",
            description = "ID를 이용해서 아이템 하나를 조회합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "400", description = "아이템 id 확인"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)")
            })
    public ResponseEntity<Object> findItemById(@PathVariable("item_id") Long id) {
        ItemDTO.ResponseItem item = itemService.findItemById(id);
        return ResponseEntity.ok(item);
    }

    @PostMapping("/create")
    @Operation(
            summary = "아이템 하나 생성",
            description = "아이템 하나를 생성합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "400", description = "인벤토리 id 확인"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)")
            })
    public ResponseEntity<String> createItem(@RequestBody ItemDTO.RequestCreateItem dto) {
        Long itemId = itemService.createItem(dto);
        return ResponseEntity.ok("아이템 생성 완료 id: " + itemId);
    }

    @PutMapping("{item_id}")
    @Operation(
            summary = "아이템 하나 수정",
            description = "아이템 하나를 수정합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "400", description = "아이템 id 확인"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)")
            })
    public ResponseEntity<String> updateItemById(@PathVariable("item_id") Long id, @RequestBody ItemDTO.RequestUpdateItem dto) {
        Long itemId = itemService.updateItemById(id, dto);
        return ResponseEntity.ok("아이템 수정 완료 id: " + itemId);
    }

    @PatchMapping("{item_id}")
    @Operation(
            summary = "아이템 하나 삭제 처리",
            description = "ID를 이용해서 아이템 하나를 삭제 처리합니다 (status = false)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "400", description = "아이템 id 확인"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)")
            })
    public ResponseEntity<String> deleteItemById(@PathVariable("item_id") Long id) {
        Long itemId = itemService.deleteItemById(id);
        return ResponseEntity.ok("아이템 삭제 처리 완료 id: " + itemId);
    }

}