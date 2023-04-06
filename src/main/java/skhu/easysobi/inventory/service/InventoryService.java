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

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;
    private final UserInventoryRepository userInventoryRepository;

    // 메인 페이지 조회, 간이 인벤토리 정보 등
    public List<InventoryDTO.ResponseMiniInventory> mainPage(Principal principal) {
        // 유저 정보
        User user = userRepository.findByEmail(principal.getName()).get();
        List<UserInventory> userInventoryList = userInventoryRepository.findByUserIdAndAccessStatus(user.getId(), true);

        // 간이 인벤토리 정보 목록
        List<InventoryDTO.ResponseMiniInventory> list = new ArrayList<>();

        // 유저가 접근할 수 있는 인벤토리 모두 접근
        for (UserInventory userInventory : userInventoryList) {
            Inventory inventory = userInventory.getInventory();
            InventoryDTO.ResponseMiniInventory responseMiniInventory = inventory.toResponseMiniInventoryDTO();

            // 인벤토리 내 아이템 목록
            List<Item> itemList = inventory.getItemList();

            // 인벤토리 내 아이템 개수
            responseMiniInventory.setItemCount(itemList.size());

            // 소비기한 > (오늘 + 7일)인 경우 목록에서 제거
            // 즉 소비기한이 오늘 기준으로 7일 이하로 남은 아이템만 남음
            itemList.removeIf(item ->
                    item.getExpDate().getNano() > LocalDateTime.now().minusDays(7).getNano());

            // 인벤토리 내 기한 만료 임박 아이템 목록
            responseMiniInventory.setImminentItemList(itemList.stream()
                    .map(Item::toResponseDTO).collect(Collectors.toList()));

            // 리스트에 추가
            list.add(responseMiniInventory);
        }

        return list;
    }

//    // 접근 가능한 인벤토리 조회
//    public List<UserInventoryDTO.ResponseUserInventory> findInventoryList(Principal principal) {
//        User user = userRepository.findByEmail(principal.getName()).get();
//        return userInventoryRepository.findByUserIdAndAccessStatus(user.getId(), true)
//                .stream().map(UserInventory::toResponseDTO).collect(Collectors.toList());
//    }

    // id로 인벤토리 조회
    public InventoryDTO.ResponseInventory findInventoryById(Long id) {
        Optional<Inventory> optionalInventory = inventoryRepository
                .findByIdAndInventoryStatus(id, true);

        // id가 일치하는 인벤토리가 있는 경우
        if (optionalInventory.isPresent()) {
            // 인벤토리 찾기
            Inventory inventory = optionalInventory.get();

            // 삭제되지 않은 아이템 리스트로 변경
            inventory.updateInventoryItemList(itemRepository.findByInventoryAndItemStatus(inventory, true));

            return inventory.toResponseDTO();
        } else {
            throw new IllegalStateException();
        }
    }

    // 인벤토리 생성
    public void createInventory(InventoryDTO.RequestCreateInventory dto, Principal principal) {
        // Inventory 만들기
        Inventory inventory = inventoryRepository.save(dto.toEntity());

        // 해당 유저 찾기
        User user = userRepository.findByEmail(principal.getName()).get();

        // 저장
        UserInventoryDTO.RequestCreateUserInventory createDto =
                new UserInventoryDTO.RequestCreateUserInventory(user, inventory);
        userInventoryRepository.save(createDto.toEntity());
    }

    // 인벤토리 업데이트
    public void updateInventoryById(Long id, InventoryDTO.RequestCreateInventory dto) {
        // id와 삭제 여부를 기준으로 인벤토리를 가져옴
        Optional<Inventory> optionalInventory = inventoryRepository.findByIdAndInventoryStatus(id, true);

        // id가 일치하는 인벤토리가 있는 경우
        if (optionalInventory.isPresent()) {
            Inventory inventory = optionalInventory.get();

            // 인벤토리 수정
            inventory.updateInventoryName(dto.getInventoryName());
            inventoryRepository.save(inventory);
        }
    }

    // 인벤토리 삭제
    public void deleteInventorById(Long id) {
        // id와 삭제 여부를 기준으로 인벤토리를 가져옴
        Optional<Inventory> optionalInventory = inventoryRepository.findByIdAndInventoryStatus(id, true);
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
            userInventoryRepository.save(userInventory);
        }
    }

}