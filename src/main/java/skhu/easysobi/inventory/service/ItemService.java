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
        Optional<Item> optionalItem = itemRepository.findByIdAndItemStatus(id, true);

        if (optionalItem.isPresent()) {
            return optionalItem.get().toResponseDTO();
        } else {
            throw new IllegalStateException("아이템을 찾을 수 없습니다");
        }
    }

    // 아이템 생성
    public Long createItem(ItemDTO.RequestCreateItem dto) {
        Optional<Inventory> optionalInventory = inventoryRepository
                .findByIdAndInventoryStatus(dto.getInventoryId(), true);

        if (optionalInventory.isPresent()) {
            dto.setInventory(optionalInventory.get());

            // 아이템의 소비기한 설정
            LocalDateTime expDate = calcExpDate(dto.getMfgDate(), categoryRepository, dto.getCategoryNum());
            dto.setExpDate(expDate);

            return itemRepository.save(dto.toEntity()).getId();
        } else {
            throw new IllegalStateException("인벤토리를 찾을 수 없습니다");
        }
    }

    // 아이템 업데이트
    public Long updateItemById(Long id, ItemDTO.RequestUpdateItem dto) {
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
            return itemRepository.save(item).getId();
        } else {
            throw new IllegalStateException("수정할 아이템을 찾을 수 없습니다");
        }
    }

    // 아이템 삭제 처리
    public Long deleteItemById(Long id) {
        // id와 삭제 여부를 기준으로 아이템을 가져옴
        Optional<Item> optionalItem = itemRepository.findByIdAndItemStatus(id, true);

        // id가 일치하는 아이템이 있는 경우
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();

            // 아이템 삭제 처리
            item.deleteItem();
            return itemRepository.save(item).getId();
        } else {
            throw new IllegalStateException("삭제할 아이템을 찾을 수 없습니다");
        }
    }

}