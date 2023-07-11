package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.Dto.CommentRequestDto;
import ru.practicum.shareit.item.Dto.ItemRequestDto;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constants.Constants.REQUEST_HEADER_FOR_USER;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemRequestDto item,
                                             @RequestHeader(REQUEST_HEADER_FOR_USER) Long userId) {
        log.info("Добаляем вещь: {}", item);
        return itemClient.createItem(userId, item);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemRequestDto item, @PathVariable Long id,
                                             @RequestHeader(REQUEST_HEADER_FOR_USER) Long userId) {
        log.info("Изменяем вещь: {}", item);
        return itemClient.updateItem(userId, id, item);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable Long id,
                                              @RequestHeader(REQUEST_HEADER_FOR_USER) Long userId) {
        log.info("Получение вещи с ид {}", id);
        return itemClient.getItemById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                           @RequestHeader(REQUEST_HEADER_FOR_USER) Long userId) {
        log.info("Получение списка всех вещей пользователя с ид {}", userId);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAvailableItem(@RequestParam("text") String text,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                   @RequestHeader(REQUEST_HEADER_FOR_USER) Long userId) {
        log.info("Получение списка всех доступных для аренды вещей содержащих текст \"{}\" в названии или описании", text);
        return itemClient.getAvailableItems(userId, text, from, size);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentRequestDto comment,
                                                @PathVariable Long id,
                                                @RequestHeader(REQUEST_HEADER_FOR_USER) Long userId) {
        log.info("Добавляем комментарий: {}", comment);
        return itemClient.commentItem(userId, id, comment);
    }
}
