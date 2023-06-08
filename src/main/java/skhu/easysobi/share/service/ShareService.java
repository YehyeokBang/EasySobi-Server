package skhu.easysobi.share.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import skhu.easysobi.auth.domain.User;
import skhu.easysobi.auth.repository.UserRepository;
import skhu.easysobi.common.exception.CustomException;
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

import static skhu.easysobi.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ShareService {

    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final UserInventoryRepository userInventoryRepository;

    // 인벤토리 공유하기
    public void shareInventory(ShareDTO.RequestShare dto) {
        Optional<User> optionalUser = userRepository.findByEmail(dto.getEmail());
        Optional<Inventory> optionalInventory = inventoryRepository.findByIdAndIsDeleted(dto.getInventoryId(), false);

        // 유저 또는 인벤토리가 없으면 예외 처리
        if (optionalUser.isEmpty()) throw new CustomException(USER_NOT_FOUND);
        if (optionalInventory.isEmpty()) throw new CustomException(INVENTORY_NOT_FOUND);

        // 인벤토리 공유 요청 추가
        userInventoryRepository.save(new UserInventoryDTO.RequestCreateUserInventory(optionalUser.get(), optionalInventory.get(), false).toEntity());
    }

    // 공유 받은 인벤토리 목록
    public List<UserInventoryDTO.ResponseUserInventory> shareList(Principal principal) {
        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());

        // 유저가 없으면 예외 처리
        if (optionalUser.isEmpty()) throw new CustomException(USER_NOT_FOUND);

        // 공유 받은 인벤토리 목록 반환
        return userInventoryRepository.findByUserIdAndIsAcceptedAndIsDeleted(optionalUser.get().getId(), false, false)
                .stream()
                .map(UserInventory::toResponseDTO)
                .collect(Collectors.toList());
    }

    // 공유 받은 인벤토리 수락
    public void acceptShare(Long userInventoryId, Principal principal) {
        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());
        Optional<Inventory> optionalInventory = inventoryRepository.findByIdAndIsDeleted(userInventoryId, false);

        // 유저 또는 인벤토리가 없으면 예외 처리
        if (optionalUser.isEmpty()) throw new CustomException(USER_NOT_FOUND);
        if (optionalInventory.isEmpty()) throw new CustomException(INVENTORY_NOT_FOUND);

        // 수락하려는 공유 받은 인벤토리
        Optional<UserInventory> optionalUserInventory = userInventoryRepository.findByUserIdAndInventoryAndIsAcceptedAndIsDeleted(optionalUser.get().getId(), optionalInventory.get(), false, true);

        // 공유 받은 인벤토리가 없으면 예외 처리
        if (optionalUserInventory.isEmpty()) throw new CustomException(USER_INVENTORY_NOT_FOUND);

        // 공유 받은 인벤토리 수락
        UserInventory userInventory = optionalUserInventory.get();
        userInventory.acceptUserInventory();
        userInventoryRepository.save(userInventory);
    }

}