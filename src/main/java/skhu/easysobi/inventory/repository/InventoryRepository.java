package skhu.easysobi.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skhu.easysobi.inventory.domain.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {



}
