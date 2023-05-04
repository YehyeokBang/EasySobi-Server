package skhu.easysobi.share.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import skhu.easysobi.auth.domain.User;
import skhu.easysobi.auth.repository.UserRepository;
import skhu.easysobi.inventory.domain.Inventory;
import skhu.easysobi.inventory.domain.UserInventory;
import skhu.easysobi.inventory.dto.UserInventoryDTO;
import skhu.easysobi.inventory.repository.InventoryRepository;
import skhu.easysobi.inventory.repository.UserInventoryRepository;
import skhu.easysobi.share.dto.ShareDTO;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShareService {
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final UserInventoryRepository userInventoryRepository;

    // 인벤토리 공유하기
    public void shareInventory(ShareDTO.RequestShare dto) {
        Optional<User> optionalUser = userRepository.findByEmail(dto.getEmail());
        Optional<Inventory> optionalInventory = inventoryRepository.findByIdAndInventoryStatus(dto.getInventoryId(), true);

        if (optionalUser.isPresent() && optionalInventory.isPresent()) {
            User user = optionalUser.get();
            Inventory inventory = optionalInventory.get();

            UserInventoryDTO.RequestCreateUserInventory createDto =
                    new UserInventoryDTO.RequestCreateUserInventory(user, inventory, false);
            userInventoryRepository.save(createDto.toEntity());
        } else {
            throw new IllegalStateException("유저 또는 인벤토리를 찾을 수 없습니다");
        }
    }

    // 공유받은 인벤토리 목록
    public List<UserInventoryDTO.ResponseUserInventory> shareList(Principal principal) {
        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return userInventoryRepository.findByUserIdAndAccessStatus(user.getId(), false)
                    .stream().map(UserInventory::toResponseDTO).collect(Collectors.toList());
        } else {
            throw new IllegalStateException("유저를 찾을 수 없습니다");
        }
    }

    // 공유받은 인벤토리 수락
    public void acceptShare(Long userInventoryId) {
        Optional<UserInventory> optionalUserInventory = userInventoryRepository.findByInventoryId(userInventoryId);

        if (optionalUserInventory.isPresent()) {
            UserInventory userInventory = optionalUserInventory.get();
            userInventory.acceptUserInventory();
            userInventoryRepository.save(userInventory);
        } else {
            throw new IllegalStateException("해당 요청을 찾을 수 없습니다");
        }
    }
}
