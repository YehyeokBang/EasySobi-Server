package skhu.easysobi.inventory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "찾을 수 없는 아이템")
            })
    public ItemDTO.ResponseItem findItemById(@PathVariable("item_id") Long id) {
        return itemService.findItemById(id);
    }

    @PostMapping("/create")
    @Operation(
            summary = "아이템 하나 생성",
            description = "아이템 하나를 생성합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public void createItem(@RequestBody ItemDTO.RequestCreateItem dto) {
        itemService.createItem(dto);
    }

    @PutMapping("{item_id}")
    @Operation(
            summary = "아이템 하나 수정",
            description = "아이템 하나를 수정합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "찾을 수 없는 아이템")
            })
    public void updateItemById(@PathVariable("item_id") Long id, @RequestBody ItemDTO.RequestCreateItem dto) {
        itemService.updateItemById(id, dto);
    }

    @PatchMapping("{item_id}")
    @Operation(
            summary = "아이템 하나 삭제 처리",
            description = "ID를 이용해서 아이템 하나를 삭제 처리합니다 (status = false)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "찾을 수 없는 아이템")
            })
    public void deleteItemById(@PathVariable("item_id") Long id) {
        itemService.deleteItemById(id);
    }

}