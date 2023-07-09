package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import java.util.List;
import java.util.stream.Collectors;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.mapper.UserMapper;

@UtilityClass
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(UserMapper.toUserDto(item.getOwner()));
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }

    public ItemDtoResponse toItemDtoResponse(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        ItemDtoResponse itemDto = new ItemDtoResponse();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        if (item != null) {
            itemDto.setOwner(UserMapper.toUserDto(item.getOwner()));
        }
        if (lastBooking != null) {
            itemDto.setLastBooking(BookingMapper.toBookingDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(BookingMapper.toBookingDto(nextBooking));
        }
        if (comments != null) {
            itemDto.setComments(comments.stream().map(e -> CommentMapper.toCommentDto(e)).collect(Collectors.toList()));
        }
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }

    public Item toItem(ItemDto itemDto) {
        if (itemDto != null) {
            Item item = new Item();
            item.setId(itemDto.getId());
            item.setName(itemDto.getName());
            item.setDescription(itemDto.getDescription());
            item.setAvailable(itemDto.getAvailable());
            item.setOwner(UserMapper.toUser(itemDto.getOwner()));
            return item;
        } else {
            return null;
        }
    }

    public Item toItemRequest(ItemDto itemDto, Request request) {
        if (itemDto != null) {
            Item item = new Item();
            item.setId(itemDto.getId());
            item.setName(itemDto.getName());
            item.setDescription(itemDto.getDescription());
            item.setAvailable(itemDto.getAvailable());
            item.setOwner(UserMapper.toUser(itemDto.getOwner()));
            item.setRequest(request);
            return item;
        } else {
            return null;
        }
    }

    public void updateItem(ItemDto itemDto, Item item) {
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
        }
    }
}
