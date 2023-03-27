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
import skhu.easysobi.inventory.repository.UserInventoryRepository;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final UserInventoryRepository userInventoryRepository;
    private final UserRepository userRepository;

    public List<UserInventoryDTO.Response> findInventoryList(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).get();
        return userInventoryRepository.findByUserId(user.getId())
                .stream().map(UserInventory::toResponseDTO).collect(Collectors.toList());
    }

    public InventoryDTO.Response findInventoryById(Long id) {
        return inventoryRepository.findById(id).get().toResponseDTO();
    }

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

}
