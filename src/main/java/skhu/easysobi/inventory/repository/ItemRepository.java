package skhu.easysobi.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skhu.easysobi.inventory.domain.Inventory;
import skhu.easysobi.inventory.domain.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // 인벤토리 id, 삭제하지 않은 인벤토리
    Optional<Item> findByIdAndIsDeleted(Long id, Boolean isDeleted);

    List<Item> findByInventoryAndIsDeleted(Inventory inventory, Boolean isDeleted);

}
