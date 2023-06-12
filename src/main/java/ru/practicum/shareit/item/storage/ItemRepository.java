package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerIdOrderById(Long id);

    @Query("select it " +
            "from Item as it "+
            "where (Lower(it.name) like '%'||Lower(?1)||'%' or Lower(it.description) like '%'||Lower(?1)||'%') "+
            "  and it.available = TRUE ")
    List<Item> findAvailableItemsByNameDescription(String text);
}
