package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItem(Item item);

    @Query("select c from Comment as c " +
            "join c.item as i " +
            "where i.id in :listItem ")
    List<Comment> findByListItems(@Param("listItem") List<Long> listItem);
}
