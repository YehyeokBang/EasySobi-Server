package skhu.easysobi.inventory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skhu.easysobi.inventory.dto.InventoryDTO;
import skhu.easysobi.inventory.dto.UserInventoryDTO;
import skhu.easysobi.inventory.service.InventoryService;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    // 조회 가능한 인벤토리 보기
    @GetMapping("")
    public List<UserInventoryDTO.Response> findInventoryList(Principal principal) {
        return inventoryService.findInventoryList(principal);
    }

    // id로 인벤토리 조회
    @GetMapping("{inventory_id}")
    public InventoryDTO.Response findInventoryById(@PathVariable("inventory_id") Long id) {
        return inventoryService.findInventoryById(id);
    }

    // 인벤토리 생성
    @PostMapping("/create")
    private ResponseEntity<String> createInventory(@RequestBody InventoryDTO.RequestCreate dto, Principal principal) {
        try {
            inventoryService.createInventory(dto, principal);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("생성 완료");
    }

    // 인벤토리 업데이트
    @PutMapping("{inventory_id}")
    public void updateInventoryById(@PathVariable("inventory_id") Long id, @RequestBody InventoryDTO.RequestCreate dto) {
        inventoryService.updateInventoryById(id, dto);
    }

    // 인벤토리 삭제 처리, 실제 삭제는 아님
    @PatchMapping("{inventory_id}")
    public void deleteInventoryById(@PathVariable("inventory_id") Long id) {
        inventoryService.deleteInventorById(id);
    }


}
