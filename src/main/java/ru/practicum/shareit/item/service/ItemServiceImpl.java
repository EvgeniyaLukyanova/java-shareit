package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.pageable.FromSizePageable;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.storage.ItemRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private final RequestRepository requestRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository repository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository,
                           RequestRepository requestRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.requestRepository = requestRepository;
    }

    @Transactional
    @Override
    public ItemDto createItem(ItemDto item, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        item.setOwner(UserMapper.toUserDto(user));
        if (item.getRequestId() != null) {
            Request request = requestRepository.findById(item.getRequestId())
                    .orElseThrow(() -> new NotFoundException(String.format("Запрос с ид %s не найден", item.getRequestId())));
            return ItemMapper.toItemDto(repository.save(ItemMapper.toItemRequest(item, request)));
        } else {
            return ItemMapper.toItemDto(repository.save(ItemMapper.toItem(item)));
        }
    }

    @Transactional
    @Override
    public ItemDto partialUpdate(ItemDto itemDto, Long id, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        Item item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ид %s не найдена", id)));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format("Пользовать с ид %s не может редактировать вещь \"%s\"", userId, itemDto.getName()));
        }
        ItemMapper.updateItem(itemDto, item);
        return ItemMapper.toItemDto(item);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDtoResponse getItemById(Long id, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        Item item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ид %s не найдена", id)));
        Booking lastBooking = bookingRepository.findByLastBooker(id, userId);
        Booking nextBooking = bookingRepository.findByNextBooker(id, userId);
        List<Comment> comments = commentRepository.findByItem(item);
        return ItemMapper.toItemDtoResponse(item, lastBooking, nextBooking, comments);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDtoResponse> getItems(Long userId, Integer from, Integer size) {
        List<Item> items = new ArrayList<>();
        if (from != null & size != null) {
            FromSizePageable page = FromSizePageable.of(from, size, Sort.by("id").descending());
            items = repository.findByOwnerId(userId, page).stream().collect(Collectors.toList());
        } else {
            items = repository.findByOwnerIdOrderById(userId);
        }
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
                    if (comment.getItem().getId().equals(item.getId())) {
                        itemComments.add(comment);
                    }
                }
                itemDtoResponses.add(ItemMapper.toItemDtoResponse(item, lastBooking, nextBooking, itemComments));
            }
        }
        return itemDtoResponses;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAvailableItems(String text, Integer from, Integer size) {
        Long pageNo = null;
        if (text.isEmpty()) {
            return new ArrayList<>();
        } else {
            if (from != null & size != null) {
                FromSizePageable page = FromSizePageable.of(from, size, Sort.by("id").descending());
                return repository.findAvailableItemsByNameDescription(text, page).stream()
                        .map(e -> ItemMapper.toItemDto(e))
                        .collect(Collectors.toList());
            } else {
                return repository.findAvailableItemsByNameDescription(text).stream()
                        .map(e -> ItemMapper.toItemDto(e))
                        .collect(Collectors.toList());
            }
        }
    }

    @Transactional
    @Override
    public CommentDto createComment(CommentDto comment, Long id, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        Item item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ид %s не найдена", id)));
        if (bookingRepository.findByListOfBookings(id, userId).size() == 0) {
            throw new ValidationException(String.format("Вещь с ид %s не бронировалась пользователем с ид %s", id, userId));
        }
        comment.setItem(ItemMapper.toItemDto(item));
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(comment, user)));
    }
}
