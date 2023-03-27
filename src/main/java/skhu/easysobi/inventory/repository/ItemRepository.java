package skhu.easysobi.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skhu.easysobi.inventory.domain.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {



}
