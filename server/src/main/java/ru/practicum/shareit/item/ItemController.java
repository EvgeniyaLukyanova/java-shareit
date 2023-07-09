package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import java.util.Collection;

import static ru.practicum.shareit.constants.Constants.requestHeaderForUser;

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
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestBody ItemDto item,
                          @RequestHeader(requestHeaderForUser) Long userId) {
        log.info("Начинаем добавлять вещь: {}", item);
        ItemDto itemDto = itemService.createItem(item, userId);
        log.info("Вещь добавлена: {}", item);
        return itemDto;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto patch(@RequestBody ItemDto item, @PathVariable Long id,
                         @RequestHeader(requestHeaderForUser) Long userId) {
        log.info("Начинаем добавлять вещь: {}", item);
        ItemDto itemDto = itemService.partialUpdate(item, id, userId);
        log.info("Вещь добавлена: {}", item);
        return itemDto;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDtoResponse getItemById(@PathVariable Long id,
                                       @RequestHeader(requestHeaderForUser) Long userId) {
        log.info("Получение вещи с ид {}", id);
        return itemService.getItemById(id, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDtoResponse> findAll(@RequestParam(required = false) Integer from,
                                               @RequestParam(required = false) Integer size,
                                               @RequestHeader(requestHeaderForUser) Long userId) {
        log.info("Получение списка всех вещей пользователя с ид {}", userId);
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> findAvailableItem(@RequestParam("text") String text,
                                                 @RequestParam(required = false) Integer from,
                                                 @RequestParam(required = false) Integer size) {
        log.info("Получение списка всех доступных для аренды вещей содержащих текст \"{}\" в названии или описании", text);
        return itemService.getAvailableItems(text, from, size);
    }

    @PostMapping("/{id}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto createComment(@RequestBody CommentDto comment,
                                    @PathVariable Long id,
                                    @RequestHeader(requestHeaderForUser) Long userId) {
        log.info("Начинаем добавлять комментарий: {}", comment);
        CommentDto commentDto = itemService.createComment(comment, id, userId);
        log.info("Комментарий добавлен: {}", comment);
        return commentDto;
    }
}
