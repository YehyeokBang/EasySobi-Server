package skhu.easysobi.inventory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import skhu.easysobi.inventory.dto.ItemDTO;
import skhu.easysobi.inventory.service.ItemService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;

    // id로 아이템 조회
    @GetMapping("/{item_id}")
    public ItemDTO.Response findItemById(@PathVariable("item_id") Long id) {
        return itemService.findItemById(id);
    }

    // 아이템 생성
    @PostMapping("/create")
    public void createItem(@RequestBody ItemDTO.RequestCreate dto) {
        itemService.createItem(dto);
    }

    // 아이템 업데이트
    @PutMapping("{item_id}")
    public void updateItemById(@PathVariable("item_id") Long id, @RequestBody ItemDTO.RequestCreate dto) {
        itemService.updateItemById(id, dto);
    }

    // 아이템 삭제 처리, 실제 삭제는 아님
    @PatchMapping("{item_id}")
    public void deleteItemById(@PathVariable("item_id") Long id) {
        itemService.deleteItemById(id);
    }

}
