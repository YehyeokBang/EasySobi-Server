package skhu.easysobi.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import skhu.easysobi.inventory.domain.Inventory;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // 인벤토리 id, 삭제하지 않은 인벤토리
    Optional<Inventory> findByIdAndIsDeleted(Long id, Boolean isDeleted);

    // 인벤토리 id, 삭제하지 않은 인벤토리, 삭제하지 않은 아이템
    Optional<Inventory> findByIdAndIsDeletedAndItemListIsDeletedFalse(Long id, boolean isDeleted);

}
