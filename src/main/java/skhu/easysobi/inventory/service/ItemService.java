package skhu.easysobi.inventory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import skhu.easysobi.inventory.domain.Inventory;
import skhu.easysobi.inventory.domain.Item;
import skhu.easysobi.inventory.dto.ItemDTO;
import skhu.easysobi.inventory.repository.InventoryRepository;
import skhu.easysobi.inventory.repository.ItemRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;

    // 아이템 조회
    public ItemDTO.ResponseItem findItemById(Long id) {
        return itemRepository.findByIdAndItemStatus(id, true).get().toResponseDTO();
    }

    // 아이템 생성
    public void createItem(ItemDTO.RequestCreateItem dto) {
        Optional<Inventory> optionalInventory = inventoryRepository
                .findByIdAndInventoryStatus(dto.getInventoryId(), true);

        if (optionalInventory.isPresent()) {
            dto.setInventory(optionalInventory.get());
            itemRepository.save(dto.toEntity());
        }
    }

    // 아이템 업데이트
    public void updateItemById(Long id, ItemDTO.RequestCreateItem dto) {
        // id와 삭제 여부를 기준으로 아이템을 가져옴
        Optional<Item> optionalItem = itemRepository.findByIdAndItemStatus(id, true);

        // id가 일치하는 아이템이 있는 경우
        if (optionalItem.isPresent()) {
            // 변경사항 입력
            Item item = optionalItem.get();
            item.setName(dto.getName());
            item.setCount(dto.getCount());
            item.setCategory(dto.getCategory());
            item.setMfgDate(dto.getMfgDate());

            // 저장
            itemRepository.save(item);
        }
    }

    // 아이템 삭제
    public void deleteItemById(Long id) {
        // id와 삭제 여부를 기준으로 아이템을 가져옴
        Optional<Item> optionalItem = itemRepository.findByIdAndItemStatus(id, true);

        // id가 일치하는 아이템이 있는 경우
        if (optionalItem.isPresent()) {
            // 아이템 상태: false 변경
            Item item = optionalItem.get();
            item.setItemStatus(false);

            // 저장
            itemRepository.save(item);
        }
    }

}