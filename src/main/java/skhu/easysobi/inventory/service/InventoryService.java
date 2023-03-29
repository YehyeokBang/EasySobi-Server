package skhu.easysobi.inventory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import skhu.easysobi.auth.domain.User;
import skhu.easysobi.auth.repository.UserRepository;
import skhu.easysobi.inventory.domain.Inventory;
import skhu.easysobi.inventory.domain.UserInventory;
import skhu.easysobi.inventory.dto.InventoryDTO;
import skhu.easysobi.inventory.dto.UserInventoryDTO;
import skhu.easysobi.inventory.repository.InventoryRepository;
import skhu.easysobi.inventory.repository.ItemRepository;
import skhu.easysobi.inventory.repository.UserInventoryRepository;

import java.security.Principal;
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

    // 접근 가능한 인벤토리 조회
    public List<UserInventoryDTO.Response> findInventoryList(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).get();
        return userInventoryRepository.findByUserIdAndAccessStatus(user.getId(), true)
                .stream().map(UserInventory::toResponseDTO).collect(Collectors.toList());
    }

    // id로 인벤토리 조회
    public InventoryDTO.Response findInventoryById(Long id) {
        Optional<Inventory> optionalInventory = inventoryRepository
                .findByIdAndInventoryStatus(id, true);

        // id가 일치하는 인벤토리가 있는 경우
        if (optionalInventory.isPresent()) {
            // 인벤토리 찾기
            Inventory inventory = optionalInventory.get();

            // 삭제되지 않은 아이템 리스트로 변경
            inventory.setItemList(itemRepository
                    .findByInventoryAndItemStatus(inventory, true));

            return inventory.toResponseDTO();
        } else {
            throw new IllegalStateException();
        }
    }

    // 인벤토리 생성
    public void createInventory(InventoryDTO.RequestCreate dto, Principal principal) {
        // Inventory 만들기
        Inventory inventory = inventoryRepository.save(dto.toEntity());

        // 해당 유저 찾기
        User user = userRepository.findByEmail(principal.getName()).get();

        // 저장
        UserInventoryDTO.RequestCreate createDto = new UserInventoryDTO.RequestCreate();
        createDto.setUser(user);
        createDto.setInventory(inventory);
        userInventoryRepository.save(createDto.toEntity());
    }

    // 인벤토리 업데이트
    public void updateInventoryById(Long id, InventoryDTO.RequestCreate dto) {
        // id와 삭제 여부를 기준으로 인벤토리를 가져옴
        Optional<Inventory> optionalInventory = inventoryRepository.findByIdAndInventoryStatus(id, true);

        // id가 일치하는 인벤토리가 있는 경우
        if (optionalInventory.isPresent()) {
            // 변경사항 입력
            Inventory inventory = optionalInventory.get();
            inventory.setInventoryName(dto.getInventoryName());

            // 저장
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
            // 인벤토리 상태: false 변경
            Inventory inventory = optionalInventory.get();
            inventory.setInventoryStatus(false);

            // 인벤토리 접근 정보 상태: false 변경
            UserInventory userInventory = optionalUserInventory.get();
            userInventory.setAccessStatus(false);

            // 저장
            inventoryRepository.save(inventory);
            userInventoryRepository.save(userInventory);
        }
    }


}
