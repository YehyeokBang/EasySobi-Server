package skhu.easysobi.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import skhu.easysobi.inventory.domain.Inventory;
import skhu.easysobi.inventory.domain.UserInventory;

import java.util.List;
import java.util.Optional;

public interface UserInventoryRepository extends JpaRepository<UserInventory, Long> {

    // 유저 id로 접근 가능한 인벤토리 찾기
    List<UserInventory> findByUserIdAndIsAcceptedAndIsDeleted(Long id, Boolean isAccepted, Boolean isDeleted);

    // 공유받은 인벤토리 중 수락하지 않은 인벤토리 찾기
    Optional<UserInventory> findByUserIdAndInventoryAndIsAcceptedAndIsDeleted(Long id, Inventory inventory, Boolean isAccepted, Boolean isDeleted);

    // id로 접근 권한 찾기
    List<UserInventory> findByInventoryId(Long id);

    @Query("SELECT ui FROM UserInventory ui JOIN ui.inventory i JOIN i.itemList item WHERE item.expDate < CURRENT_DATE")
    List<UserInventory> findAllByExpiredItemsInInventory();

}
