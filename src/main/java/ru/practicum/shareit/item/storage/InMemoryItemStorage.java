package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryItemStorage implements ItemStorage {

    private Long uniqueId = Long.valueOf(0);
    public final Map<Long, Item> items = new HashMap<>();

    private Long getUniqueId() {
        uniqueId++;
        return uniqueId;
    }

    @Override
    public Item createItem(Item item) {
        if (item != null) {
            item.setId(getUniqueId());
            items.put(item.getId(), item);
        }
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        if (item != null) {
            items.put(item.getId(), item);
        }
        return item;
    }

    @Override
    public Item getItemById(Long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getItems() {
        return new ArrayList<>(items.values());
    }
}
