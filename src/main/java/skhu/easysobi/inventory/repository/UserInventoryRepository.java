package skhu.easysobi.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skhu.easysobi.inventory.domain.Inventory;
import skhu.easysobi.inventory.domain.UserInventory;

import java.util.List;
import java.util.Optional;

public interface UserInventoryRepository extends JpaRepository<UserInventory, Long> {

    // 유저 id로 접근 가능한 인벤토리 찾기
    List<UserInventory> findByUserIdAndAccessStatus(Long id, Boolean status);

    // 공유받은 인벤토리 중 수락하지 않은 인벤토리 찾기
    Optional<UserInventory> findByUserIdAndAccessStatusAndInventory(Long id, Boolean status, Inventory inventory);

    // id로 인벤토리 찾기
    Optional<UserInventory> findByInventoryId(Long id);

}
