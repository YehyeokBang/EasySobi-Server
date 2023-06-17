package skhu.easysobi.inventory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import skhu.easysobi.common.exception.CustomException;
import skhu.easysobi.inventory.domain.Inventory;
import skhu.easysobi.inventory.domain.Item;
import skhu.easysobi.inventory.dto.ItemDTO;
import skhu.easysobi.inventory.repository.CategoryRepository;
import skhu.easysobi.inventory.repository.InventoryRepository;
import skhu.easysobi.inventory.repository.ItemRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static skhu.easysobi.common.ExpDate.calcExpDate;
import static skhu.easysobi.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;
    private final CategoryRepository categoryRepository;

    // 아이템 조회
    public ItemDTO.ResponseItem findItemById(Long id) {
        Optional<Item> optionalItem = itemRepository.findByIdAndIsDeleted(id, false);

        // 아이템이 없는 경우 예외 발생
        if (optionalItem.isEmpty()) throw new CustomException(ITEM_NOT_FOUND);

        return optionalItem.get().toResponseDTO();
    }

    // 아이템 생성
    public Long createItem(ItemDTO.RequestCreateItem dto) {
        Optional<Inventory> optionalInventory = inventoryRepository.findByIdAndIsDeleted(dto.getInventoryId(), false);

        // 인벤토리가 없는 경우 예외 발생
        if (optionalInventory.isEmpty()) throw new CustomException(INVENTORY_NOT_FOUND);

        // 인벤토리가 있는 경우
        dto.setInventory(optionalInventory.get());

        // 아이템의 소비기한 설정
        LocalDateTime expDate = calcExpDate(dto.getMfgDate(), categoryRepository, dto.getCategoryNum());
        dto.setExpDate(expDate);

        return itemRepository.save(dto.toEntity()).getId();
    }

    // 아이템 업데이트
    public Long updateItemById(Long id, ItemDTO.RequestUpdateItem dto) {
        // id와 삭제 여부를 기준으로 아이템을 가져옴
        Optional<Item> optionalItem = itemRepository.findByIdAndIsDeleted(id, false);

        // id가 일치하는 아이템이 없는 경우 예외 발생
        if (optionalItem.isEmpty()) throw new CustomException(ITEM_NOT_FOUND);

        // 아이템이 있는 경우
        Item item = optionalItem.get();

        // 아이템의 소비기한 설정
        LocalDateTime expDate = calcExpDate(dto.getMfgDate(), categoryRepository, dto.getCategoryNum());
        dto.setExpDate(expDate);

        // 아이템 수정
        item.updateItem(dto.getName(), dto.getCategoryNum(), dto.getCount(), dto.getMfgDate(), dto.getExpDate());
        return itemRepository.save(item).getId();
    }

    // 아이템 삭제 처리
    public Long deleteItemById(Long id) {
        // id와 삭제 여부를 기준으로 아이템을 가져옴
        Optional<Item> optionalItem = itemRepository.findByIdAndIsDeleted(id, false);

        // id가 일치하는 아이템이 없는 경우 예외 발생
        if (optionalItem.isEmpty()) throw new CustomException(ITEM_NOT_FOUND);

        // 아이템이 있는 경우
        Item item = optionalItem.get();

        // 아이템 삭제 처리
        item.deleteItem();
        return itemRepository.save(item).getId();
    }

}