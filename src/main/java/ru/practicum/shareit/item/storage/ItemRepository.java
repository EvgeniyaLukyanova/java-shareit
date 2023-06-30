package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerIdOrderById(Long id);

    Page<Item> findByOwnerId(Long id, Pageable page);

    @Query("select it " +
            "from Item as it " +
            "where (Lower(it.name) like '%'||Lower(:text)||'%' or Lower(it.description) like '%'||Lower(:text)||'%') " +
            "  and it.available = TRUE ")
    Page<Item> findAvailableItemsByNameDescription(@Param("text") String text, @Param("page") Pageable page);

    @Query("select it " +
            "from Item as it " +
            "where (Lower(it.name) like '%'||Lower(:text)||'%' or Lower(it.description) like '%'||Lower(:text)||'%') " +
            "  and it.available = TRUE ")
    List<Item> findAvailableItemsByNameDescription(@Param("text") String text);

    List<Item> findByRequestIdInOrderById(List<Long> requestIds);
}
