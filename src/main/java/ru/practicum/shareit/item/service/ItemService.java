package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.storage.ItemRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import static ru.practicum.shareit.item.mapper.CommentMapper.toComment;
import static ru.practicum.shareit.item.mapper.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;
import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;

@Service
public class ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemService(ItemRepository repository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    public ItemDto createItem(ItemDto item, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        item.setOwner(toUserDto(user));
        return toItemDto(repository.save(toItem(item)));
    }

    public ItemDto partialUpdate(ItemDto itemDto, Long id, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        Item item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ид %s не найдена", id)));
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException(String.format("Пользовать с ид %s не может редактировать вещь \"%s\"", userId, itemDto.getName()));
        }
        updateItem(itemDto, item);
        return toItemDto(repository.save(item));
    }

    @Transactional
    public ItemDtoResponse getItemById(Long id, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        Item item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ид %s не найдена", id)));
        Booking lastBooking = bookingRepository.findByLastBooker(id, userId);
        Booking nextBooking = bookingRepository.findByNextBooker(id, userId);
        List<Comment> comments = commentRepository.findByItem(item);
        return toItemDtoResponse(item, lastBooking, nextBooking, comments);
    }

    @Transactional
    public List<ItemDtoResponse> getItems(Long userId) {
        List<Item> items = repository.findByOwnerIdOrderById(userId);
        List<ItemDtoResponse> itemDtoResponses = new ArrayList<>();
        if (items.size() != 0) {
            List<Booking> lastBookings = bookingRepository.findByListLastBooker(userId);
            Map<Item, Booking> mapLastBooking = new HashMap<>();
            for (Booking booking : lastBookings) {
                mapLastBooking.put(booking.getItem(), booking);
            }
            List<Booking> nextBookings = bookingRepository.findByListNextBooker(userId);
            Map<Item, Booking> mapNextBooking = new HashMap<>();
            for (Booking booking : nextBookings) {
                mapNextBooking.put(booking.getItem(), booking);
            }
            List<Comment> comments = commentRepository.findByListItems(items.stream().map(e -> e.getId()).collect(Collectors.toList()));
            for (Item item : items) {
                Booking lastBooking = mapLastBooking.get(item);
                Booking nextBooking = mapNextBooking.get(item);
                List<Comment> itemComments = new ArrayList<>();
                for (Comment comment : comments) {
                    if (comment.getItem().getId() == item.getId()) {
                        itemComments.add(comment);
                    }
                }
                itemDtoResponses.add(toItemDtoResponse(item, lastBooking, nextBooking, itemComments));
            }
        }
        return itemDtoResponses;
    }

    public List<ItemDto> getAvailableItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        } else {
            return repository.findAvailableItemsByNameDescription(text).stream()
                    .map(e -> toItemDto(e))
                    .collect(Collectors.toList());
       }
    }

    public CommentDto createComment(CommentDto comment, Long id, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        Item item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ид %s не найдена", id)));
        if (bookingRepository.findByListOfBookings(id, userId).size() == 0) {
            throw new ValidationException(String.format("Вещь с ид %s не бронировалась пользователем с ид %s", id, userId));
        }
        comment.setItem(toItemDto(item));
        comment.setCreated(LocalDateTime.now());
        return toCommentDto(commentRepository.save(toComment(comment, user)));
    }
}
