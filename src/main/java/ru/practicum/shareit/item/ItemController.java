package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }
    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Начинаем добавлять вещь: {}", item);
        ItemDto itemDto = itemService.createItem(item, userId);
        log.info("Вещь добавлена: {}", item);
        return itemDto;
    }

    @PatchMapping("/{id}")
    public ItemDto patch(@RequestBody ItemDto item, @PathVariable int id, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Начинаем добавлять вещь: {}", item);
        ItemDto itemDto = itemService.partialUpdate(item, id, userId);
        log.info("Вещь добавлена: {}", item);
        return itemDto;
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable int id) {
        log.info("Получение вещи с ид {}", id);
        return itemService.getItemById(id);
    }

    @GetMapping
    public Collection<ItemDto> findAll(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получение списка всех вещей пользователя с ид {}", userId);
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findAvailableItem(@RequestParam("text") String text) {
        log.info("Получение списка всех доступных для аренды вещей содержащих текст \"{}\" в названии или описании", text);
        return itemService.getAvailableItems(text);
    }
}
