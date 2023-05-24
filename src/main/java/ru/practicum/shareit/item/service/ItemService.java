package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemService(ItemStorage itemStorage, UserStorage userStorage, ItemMapper itemMapper) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.itemMapper = itemMapper;
    }

    public ItemDto createItem(ItemDto item, int userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Пользовать с ид %s не найден", userId));
        }
        item.setOwner(userStorage.getUserById(userId));
        return itemMapper.toItemDto(itemStorage.createItem(itemMapper.toItem(item)));
    }

    public ItemDto partialUpdate(ItemDto itemDto, int id, int userId) {
        Item item = itemStorage.getItemById(id);
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException(String.format("Пользовать с ид %s не может редактировать вещь \"%s\"", userId, itemDto.getName()));
        }
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Пользовать с ид %s не найден", userId));
        }

        itemDto.setOwner(user);

        itemMapper.updateItem(itemDto, item);
        return itemMapper.toItemDto(itemStorage.updateItem(item));
    }

    public ItemDto getItemById(int id) {
        Item item = itemStorage.getItemById(id);
        if (item == null) {
            throw new NotFoundException(String.format("Вещь с ид %s не найдена", id));
        }
        return itemMapper.toItemDto(item);
    }

    public List<ItemDto> getItems(int userId) {
        return itemStorage.getItems().stream()
                .filter(f -> f.getOwner().getId() == userId)
                .map(e -> itemMapper.toItemDto(e))
                .collect(Collectors.toList());
    }

    public List<ItemDto> getAvailableItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        } else {
            return itemStorage.getItems().stream()
                   .filter(f -> f.getName().toLowerCase().contains(text.toLowerCase()) || f.getDescription().toLowerCase().contains(text.toLowerCase()))
                   .filter(f -> f.getAvailable().booleanValue())
                   .map(e -> itemMapper.toItemDto(e))
                   .collect(Collectors.toList());
       }
    }
}
