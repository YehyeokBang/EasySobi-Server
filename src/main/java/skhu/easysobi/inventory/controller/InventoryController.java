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

    @GetMapping("")
    public List<UserInventoryDTO.Response> findInventoryList(Principal principal) {
        return inventoryService.findInventoryList(principal);
    }

    @GetMapping("{inventory_id}")
    public InventoryDTO.Response findInventoryById(@PathVariable("inventory_id") Long id) {
        return inventoryService.findInventoryById(id);
    }

    @PostMapping("/create")
    private ResponseEntity<String> createInventory(@RequestBody InventoryDTO.RequestCreate dto, Principal principal) {
        try {
            inventoryService.createInventory(dto, principal);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("생성 완료");
    }


}
