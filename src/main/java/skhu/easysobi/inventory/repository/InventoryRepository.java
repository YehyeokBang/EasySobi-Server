package skhu.easysobi.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skhu.easysobi.inventory.domain.Inventory;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // 인벤토리 id, 삭제하지 않은 인벤토리
    Optional<Inventory> findByIdAndInventoryStatus(Long aLong, Boolean status);

}
