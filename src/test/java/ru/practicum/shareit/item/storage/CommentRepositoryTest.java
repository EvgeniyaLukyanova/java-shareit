package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    @Test
    void findByListItems() {
        User user1 = new User();
        user1.setEmail("user1@user.com");
        user1.setName("user1");
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("use2r@user.com");
        user2.setName("user2");
        userRepository.save(user2);

        User commenter = new User();
        commenter.setEmail("commenter@user.com");
        commenter.setName("commenter");
        userRepository.save(commenter);

        Item item1 = new Item();
        item1.setName("Щётка для обуви");
        item1.setDescription("Щётка для обуви1");
        item1.setAvailable(true);
        item1.setOwner(user1);
        Item item1Result = itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Клей Момент");
        item2.setDescription("Клей Момент");
        item2.setAvailable(true);
        item2.setOwner(user2);
        Item item2Result = itemRepository.save(item2);

        Comment comment1 = new Comment();
        comment1.setCreated(LocalDateTime.of(2023,6,21,11,0,0));
        comment1.setAuthor(commenter);
        comment1.setText("Add comment 1");
        comment1.setItem(item1);
        commentRepository.save(comment1);

        Comment comment2 = new Comment();
        comment2.setCreated(LocalDateTime.of(2023,6,21,11,0,0));
        comment2.setAuthor(commenter);
        comment2.setText("Add comment 2");
        comment2.setItem(item2);
        commentRepository.save(comment2);

        List<Comment> comments = commentRepository.findByListItems(List.of(item1Result.getId()));
        assertEquals(1, comments.size());
        assertEquals(comment1.getText(), comments.get(0).getText());
    }
}