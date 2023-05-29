package skhu.easysobi.inventory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import skhu.easysobi.auth.domain.User;
import skhu.easysobi.auth.repository.UserRepository;
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
        // 유저 정보
        User user = userRepository.findByEmail(principal.getName()).get();
        List<UserInventory> userInventoryList = userInventoryRepository.findByUserIdAndIsAcceptAndIsDeleted(user.getId(), true,true);

        // 간이 인벤토리 정보 목록
        List<InventoryDTO.ResponseMiniInventory> list = new ArrayList<>();

        // 유저가 접근할 수 있는 인벤토리 모두 접근
        for (UserInventory userInventory : userInventoryList) {
            Inventory inventory = userInventory.getInventory();
            InventoryDTO.ResponseMiniInventory responseMiniInventory = inventory.toResponseMiniInventoryDTO();

            // 인벤토리 내 아이템 목록
            List<Item> itemList = itemRepository.findByInventoryAndIsDeleted(inventory, true);

            // 인벤토리 내 아이템 개수
            responseMiniInventory.setItemCount(itemList.size());

            // 소비기한 > (오늘 + 7일)인 경우 목록에서 제거
            // 즉 소비기한이 오늘 기준으로 7일 이하로 남은 아이템만 남음
            itemList.removeIf(item ->
                    (item.getExpDate().isAfter(LocalDateTime.now().plusDays(7))));

            // 인벤토리 내 기한 만료 임박 아이템 목록
            responseMiniInventory.setImminentItemList(itemList.stream()
                    .map(Item::toResponseDTO).collect(Collectors.toList()));

            // 리스트에 추가
            list.add(responseMiniInventory);
        }

        return list;
    }

    // id로 인벤토리 조회
    public InventoryDTO.ResponseInventory findInventoryById(Long id) {
        Optional<Inventory> optionalInventory = inventoryRepository
                .findByIdAndItemsIsDeleted(id, true);

        // id가 일치하는 인벤토리가 있는 경우
        if (optionalInventory.isPresent()) {
            // 인벤토리 DTO 변환 후 반환
            Inventory inventory = optionalInventory.get();
            return inventory.toResponseDTO();
        } else {
            throw new IllegalStateException("인벤토리를 찾을 수 없습니다");
        }
    }

    // 인벤토리 생성
    public Long createInventory(InventoryDTO.RequestInventory dto, Principal principal) {
        // Inventory 만들기
        Inventory inventory = inventoryRepository.save(dto.toEntity());

        // 해당 유저 찾기
        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            UserInventoryDTO.RequestCreateUserInventory createDto =
                    new UserInventoryDTO.RequestCreateUserInventory(user, inventory, true);
            return userInventoryRepository.save(createDto.toEntity()).getId();
        } else {
            throw new IllegalStateException("유저를 찾을 수 없습니다");
        }
    }

    // 인벤토리 업데이트
    public Long updateInventoryById(Long id, InventoryDTO.RequestInventory dto) {
        // id와 삭제 여부를 기준으로 인벤토리를 가져옴
        Optional<Inventory> optionalInventory = inventoryRepository.findByIdAndIsDeleted(id, true);

        // id가 일치하는 인벤토리가 있는 경우
        if (optionalInventory.isPresent()) {
            Inventory inventory = optionalInventory.get();

            // 인벤토리 수정
            inventory.updateInventoryName(dto.getInventoryName());
            return inventoryRepository.save(inventory).getId();
        } else {
            throw new IllegalStateException("인벤토리를 찾을 수 없습니다");
        }
    }

    // 인벤토리 삭제
    public Long deleteInventorById(Long id, Principal principal) throws ExecutionException, InterruptedException {
        // id와 삭제 여부를 기준으로 인벤토리를 가져옴
        Optional<Inventory> optionalInventory = inventoryRepository.findByIdAndIsDeleted(id, true);
        Optional<UserInventory> optionalUserInventory = userInventoryRepository.findByInventoryId(id);

        // id가 일치하는 인벤토리가 있는 경우
        if (optionalInventory.isPresent() && optionalUserInventory.isPresent()) {
            // 인벤토리 삭제 처리
            Inventory inventory = optionalInventory.get();
            inventory.deleteInventory();
            inventoryRepository.save(inventory);

            // 인벤토리 접근 불가 처리
            UserInventory userInventory = optionalUserInventory.get();
            userInventory.deleteUserInventory();

            // 인벤토리 내 아이템도 모두 삭제 처리
            List<Item> itemList = inventory.getItemList();
            for (Item item : itemList) {
                item.deleteItem();
                itemRepository.save(item);
            }
            pushService.sendDeleteInventoryMessage(principal, inventory.getInventoryName());
            return userInventoryRepository.save(userInventory).getId();
        } else {
            throw new IllegalStateException("인벤토리를 찾을 수 없습니다");
        }
    }

}