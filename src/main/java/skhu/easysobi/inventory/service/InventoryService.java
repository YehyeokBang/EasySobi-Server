package skhu.easysobi.inventory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import skhu.easysobi.auth.domain.User;
import skhu.easysobi.auth.repository.UserRepository;
import skhu.easysobi.common.exception.CustomException;
import skhu.easysobi.inventory.domain.Inventory;
import skhu.easysobi.inventory.domain.Item;
import skhu.easysobi.inventory.domain.UserInventory;
import skhu.easysobi.inventory.dto.InventoryDTO;
import skhu.easysobi.inventory.dto.UserInventoryDTO;
import skhu.easysobi.inventory.repository.InventoryRepository;
import skhu.easysobi.inventory.repository.ItemRepository;
import skhu.easysobi.inventory.repository.UserInventoryRepository;
import skhu.easysobi.push.service.PushService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static skhu.easysobi.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;
    private final UserInventoryRepository userInventoryRepository;
    private final PushService pushService;

    // 메인 페이지 조회, 간이 인벤토리 정보 등
    public List<InventoryDTO.ResponseMiniInventory> mainPage(Principal principal) {

        // 유저 정보 조회
        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());

        // 유저 정보가 없으면 예외 발생
        if (optionalUser.isEmpty()) throw new CustomException(USER_NOT_FOUND);

        // 유저가 있는 경우
        User user = optionalUser.get();

        // 유저가 접근할 수 있는 인벤토리 목록
        List<UserInventory> userInventoryList = userInventoryRepository.findByUserIdAndIsAcceptedAndIsDeleted(user.getId(), true,false);

        // 간이 인벤토리 정보 목록
        List<InventoryDTO.ResponseMiniInventory> list = new ArrayList<>();

        // 유저가 접근할 수 있는 인벤토리 모두 접근
        for (UserInventory userInventory : userInventoryList) {
            Inventory inventory = userInventory.getInventory();
            InventoryDTO.ResponseMiniInventory responseMiniInventory = inventory.toResponseMiniInventoryDTO();

            // 인벤토리 내 아이템 목록
            List<Item> itemList = itemRepository.findByInventoryIdAndIsDeletedFalse(inventory.getId());

            // 인벤토리 내 아이템 개수
            responseMiniInventory.setItemCount(itemList.size());

            // 소비기한 > (오늘 + 7일)인 경우 목록에서 제거
            // 즉 소비기한이 오늘 기준으로 7일 이하로 남은 아이템만 남음
            itemList.removeIf(item -> (item.getExpDate().isAfter(LocalDateTime.now().plusDays(7))));

            // 인벤토리 내 기한 만료 임박 아이템 목록
            responseMiniInventory.setImminentItemList(itemList.stream().map(Item::toResponseDTO).collect(Collectors.toList()));

            // 리스트에 추가
            list.add(responseMiniInventory);
        }

        return list;
    }

    // id로 인벤토리 조회
    public InventoryDTO.ResponseInventory findInventoryById(Long id) {
        Optional<Inventory> optionalInventory = inventoryRepository.findByIdAndIsDeleted(id, false);

        // id가 일치하는 인벤토리가 없는 경우 예외 발생
        if (optionalInventory.isEmpty()) throw new CustomException(INVENTORY_NOT_FOUND);

        // 인벤토리 DTO 변환 후 반환
        Inventory inventory = optionalInventory.get();
        inventory.updateInventoryItemList(itemRepository.findByInventoryIdAndIsDeletedFalse(id));
        return inventory.toResponseDTO();
    }

    // 인벤토리 생성
    public Long createInventory(InventoryDTO.RequestInventory dto, Principal principal) {
        // Inventory 만들기
        Inventory inventory = inventoryRepository.save(dto.toEntity());

        // 해당 유저 찾기
        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());

        // 유저가 없는 경우 예외 발생
        if (optionalUser.isEmpty()) throw new CustomException(USER_NOT_FOUND);

        // 유저가 있는 경우
        User user = optionalUser.get();

        // 인벤토리 접근 정보 만들기
        UserInventoryDTO.RequestCreateUserInventory createDto =
                new UserInventoryDTO.RequestCreateUserInventory(user, inventory, true);
        return userInventoryRepository.save(createDto.toEntity()).getId();
    }

    // 인벤토리 업데이트
    public Long updateInventoryById(Long id, InventoryDTO.RequestInventory dto) {
        // id와 삭제 여부를 기준으로 인벤토리를 가져옴
        Optional<Inventory> optionalInventory = inventoryRepository.findByIdAndIsDeleted(id, false);

        // id가 일치하는 인벤토리가 없는 경우 예외 발생
        if (optionalInventory.isEmpty()) throw new CustomException(INVENTORY_NOT_FOUND);

        // 인벤토리가 있는 경우
        Inventory inventory = optionalInventory.get();

        // 인벤토리 수정
        inventory.updateInventoryName(dto.getInventoryName());
        return inventoryRepository.save(inventory).getId();
    }

    // 인벤토리 삭제
    public void deleteInventorById(Long id, Principal principal) {
        // id와 삭제 여부를 기준으로 인벤토리를 가져옴
        Optional<Inventory> optionalInventory = inventoryRepository.findByIdAndIsDeleted(id, false);
        List<UserInventory> userInventoryList = userInventoryRepository.findByInventoryId(id);

        // id가 일치하는 인벤토리가 없는 경우 예외 발생
        if (optionalInventory.isEmpty()) throw new CustomException(INVENTORY_NOT_FOUND);

        // 인벤토리가 있는 경우
        Inventory inventory = optionalInventory.get();

        // 인벤토리 삭제 처리
        inventory.deleteInventory();
        inventoryRepository.save(inventory);

        // 인벤토리 내 아이템 삭제 처리
        inventory.getItemList().forEach(item -> {
            item.deleteItem();
            itemRepository.save(item);
        });

        // 인벤토리 접근 불가 처리
        userInventoryList.forEach(ui -> {
            ui.deleteUserInventory();
            userInventoryRepository.save(ui);

            // 푸시 메시지 전송
            try {
                pushService.sendDeleteInventoryMessage(principal, inventory.getInventoryName());
            } catch (ExecutionException | InterruptedException e) {
                throw new CustomException(INVALID_PUSH_MESSAGE);
            }
        });
    }

}