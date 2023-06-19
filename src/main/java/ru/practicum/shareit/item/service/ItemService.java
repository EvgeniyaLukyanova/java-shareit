package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto item, Long userId);

    ItemDto partialUpdate(ItemDto itemDto, Long id, Long userId);

    ItemDtoResponse getItemById(Long id, Long userId);

    List<ItemDtoResponse> getItems(Long userId);

    List<ItemDto> getAvailableItems(String text);

    CommentDto createComment(CommentDto comment, Long id, Long userId);
}
