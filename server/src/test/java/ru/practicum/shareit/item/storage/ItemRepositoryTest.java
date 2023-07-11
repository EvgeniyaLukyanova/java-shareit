package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    @Test
    void findAvailableItemsByNameDescriptionPage() {
        Integer from = 0;
        Integer size = 1;

        User user = new User();
        user.setEmail("user@user.com");
        user.setName("user");
        userRepository.save(user);

        Item item1 = new Item();
        item1.setName("Щётка для обуви");
        item1.setDescription("Щётка для обуви1");
        item1.setAvailable(true);
        item1.setOwner(user);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Клей Момент");
        item2.setDescription("Клей Момент1");
        item2.setAvailable(true);
        item2.setOwner(user);
        itemRepository.save(item2);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").descending());
        Page<Item> pgItem1 = itemRepository.findAvailableItemsByNameDescription("момент", page);

        assertEquals(1, pgItem1.toList().size());
        assertEquals(item2.getName(), pgItem1.toList().get(0).getName());

        Page<Item> pgItem2 = itemRepository.findAvailableItemsByNameDescription("обуви1", page);

        assertEquals(1, pgItem1.toList().size());
        assertEquals(item1.getDescription(), pgItem2.toList().get(0).getDescription());

        Page<Item> pgItem3 = itemRepository.findAvailableItemsByNameDescription("абв", page);

        assertEquals(0, pgItem3.toList().size());
    }

    @Test
    void findAvailableItemsByNameDescriptionList() {
        User user = new User();
        user.setEmail("user@user.com");
        user.setName("user");
        userRepository.save(user);

        Item item1 = new Item();
        item1.setName("Щётка для обуви");
        item1.setDescription("Щётка для обуви1");
        item1.setAvailable(true);
        item1.setOwner(user);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Клей Момент");
        item2.setDescription("Клей Момент1");
        item2.setAvailable(true);
        item2.setOwner(user);
        itemRepository.save(item2);

        List<Item> items1 = itemRepository.findAvailableItemsByNameDescription("момент");
        assertEquals(1, items1.size());
        assertEquals(item2.getName(), items1.get(0).getName());

        List<Item> items2 = itemRepository.findAvailableItemsByNameDescription("обуви1");
        assertEquals(1, items2.size());
        assertEquals(item1.getDescription(), items2.get(0).getDescription());

        List<Item> items3 = itemRepository.findAvailableItemsByNameDescription("абв");
        assertEquals(0, items3.size());
    }
}