package skhu.easysobi.inventory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import skhu.easysobi.inventory.domain.Inventory;
import skhu.easysobi.inventory.domain.Item;
import skhu.easysobi.inventory.dto.ItemDTO;
import skhu.easysobi.inventory.repository.CategoryRepository;
import skhu.easysobi.inventory.repository.InventoryRepository;
import skhu.easysobi.inventory.repository.ItemRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static skhu.easysobi.common.ExpDate.calcExpDate;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;
    private final CategoryRepository categoryRepository;

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

            // 아이템의 소비기한 설정
            LocalDateTime expDate = calcExpDate(dto.getMfgDate(), categoryRepository, dto.getCategoryNum());
            System.out.println(expDate);
            dto.setExpDate(expDate);

            itemRepository.save(dto.toEntity());
        }
    }

    // 아이템 업데이트
    public void updateItemById(Long id, ItemDTO.RequestCreateItem dto) {
        // id와 삭제 여부를 기준으로 아이템을 가져옴
        Optional<Item> optionalItem = itemRepository.findByIdAndItemStatus(id, true);

        // id가 일치하는 아이템이 있는 경우
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();

            // 아이템의 소비기한 설정
            LocalDateTime expDate = calcExpDate(dto.getMfgDate(), categoryRepository, dto.getCategoryNum());
            dto.setExpDate(expDate);

            // 아이템 수정
            item.updateItem(dto.getName(), dto.getCategoryNum(), dto.getCount(), dto.getMfgDate(), dto.getExpDate());
            itemRepository.save(item);
        }
    }

    // 아이템 삭제 처리
    public void deleteItemById(Long id) {
        // id와 삭제 여부를 기준으로 아이템을 가져옴
        Optional<Item> optionalItem = itemRepository.findByIdAndItemStatus(id, true);

        // id가 일치하는 아이템이 있는 경우
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();

            // 아이템 삭제 처리
            item.deleteItem();
            itemRepository.save(item);
        }
    }

}