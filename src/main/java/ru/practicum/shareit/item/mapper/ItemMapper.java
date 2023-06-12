package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import java.util.List;
import java.util.stream.Collectors;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDto;
import static ru.practicum.shareit.item.mapper.CommentMapper.toCommentDto;
import static ru.practicum.shareit.user.mapper.UserMapper.*;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(toUserDto(item.getOwner()));
        return itemDto;
    }

    public static ItemDtoResponse toItemDtoResponse(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        ItemDtoResponse itemDto = new ItemDtoResponse();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        if (item != null) {
            itemDto.setOwner(toUserDto(item.getOwner()));
        }
        if (lastBooking != null) {
            itemDto.setLastBooking(toBookingDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(toBookingDto(nextBooking));
        }
        if (comments != null) {
            itemDto.setComments(comments.stream().map(e -> toCommentDto(e)).collect(Collectors.toList()));
        }
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        if (itemDto != null) {
            Item item = new Item();
            item.setId(itemDto.getId());
            item.setName(itemDto.getName());
            item.setDescription(itemDto.getDescription());
            item.setAvailable(itemDto.getAvailable());
            item.setOwner(toUser(itemDto.getOwner()));
            return item;
        } else {
            return null;
        }
    }

    public static void updateItem(ItemDto itemDto, Item item) {
        if (itemDto != null) {
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }

            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }

            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }

            if (itemDto.getOwner() != null) {
                item.setOwner(toUser(itemDto.getOwner()));
            }
        }
    }
}
