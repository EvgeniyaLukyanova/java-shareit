package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.reference.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.pageable.FromSizePageable;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    ItemRepository repository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    RequestRepository requestRepository;
    private ItemService itemService;
    private User user;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(repository,
                userRepository,
                bookingRepository,
                commentRepository,
                requestRepository);

        user = new User(
                1L,
                "user@user.com",
                "user");
    }

    @Test
    void createItem() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User requestor = new User(2L, "requestor@user.com", "requestor");
        Long requestId = 1L;
        Request request = new Request();
        request.setId(requestId);
        request.setDescription("Хотел бы воспользоваться щёткой для обуви");
        request.setCreated(LocalDateTime.of(2023,6,17,10,0,0));
        request.setRequestor(requestor);

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        Long itemId = 1L;
        Item item = new Item(itemId,
                "Аккумуляторная дрель",
                "Аккумуляторная дрель + аккумулятор",
                true,
                user,
                request);

        when(repository.save(any())).thenReturn(item);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        ItemDto resultItemDto = itemService.createItem(itemDto, userId);

        assertEquals(itemDto, resultItemDto);
        verify(repository).save(item);
    }

    @Test
    void partialUpdate() {
        Long userId = 1L;
        Long itemId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Item item = new Item();
        item.setId(itemId);
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        item.setAvailable(true);
        item.setOwner(user);
        when(repository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDto updateItemDto = new ItemDto();
        updateItemDto.setId(itemId);
        updateItemDto.setName("update Аккумуляторная дрель");
        updateItemDto.setDescription("update Аккумуляторная дрель + аккумулятор");
        updateItemDto.setAvailable(false);

        ItemDto resultItemDto = itemService.partialUpdate(updateItemDto, itemId, userId);

        assertEquals(userId, resultItemDto.getId());
        assertEquals("update Аккумуляторная дрель", resultItemDto.getName());
        assertEquals("update Аккумуляторная дрель + аккумулятор", resultItemDto.getDescription());
        assertEquals(false, resultItemDto.getAvailable());
        assertEquals(UserMapper.toUserDto(user), resultItemDto.getOwner());
    }

    @Test
    void getItemById() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        item.setAvailable(true);
        item.setOwner(user);
        when(repository.findById(itemId)).thenReturn(Optional.of(item));

        User booker = new User(2L, "booker@user.com", "booker");
        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setStartDate(LocalDateTime.of(2023,6,16,10,0,0));
        lastBooking.setEndDate(LocalDateTime.of(2023,6,17,10,0,0));
        lastBooking.setBooker(booker);
        lastBooking.setItem(item);
        lastBooking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findByLastBooker(itemId, userId)).thenReturn(lastBooking);

        Booking nextBooking = new Booking();
        nextBooking.setId(1L);
        nextBooking.setStartDate(LocalDateTime.of(2023,6,20,10,0,0));
        nextBooking.setEndDate(LocalDateTime.of(2023,6,21,10,0,0));
        nextBooking.setBooker(booker);
        nextBooking.setItem(item);
        nextBooking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findByNextBooker(itemId, userId)).thenReturn(nextBooking);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setCreated(LocalDateTime.of(2023,6,21,11,0,0));
        comment.setAuthor(booker);
        comment.setText("Add comment");
        comment.setItem(item);
        when(commentRepository.findByItem(item)).thenReturn(List.of(comment));

        ItemDtoResponse itemDtoResponse = itemService.getItemById(itemId, userId);

        assertEquals(userId, itemDtoResponse.getId());
        assertEquals(BookingMapper.toBookingDto(lastBooking), itemDtoResponse.getLastBooking());
        assertEquals(BookingMapper.toBookingDto(nextBooking), itemDtoResponse.getNextBooking());
        assertEquals(1, itemDtoResponse.getComments().size());
        assertEquals(CommentMapper.toCommentDto(comment), itemDtoResponse.getComments().get(0));
    }

    @Test
    void getItems() {
        Long userId = 1L;

        Long itemId = 1L;
        Integer from = null;
        Integer size = null;

        Item item = new Item();
        item.setId(itemId);
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        item.setAvailable(true);
        item.setOwner(user);

        List<Item> items =  List.of(item);
        when(repository.findByOwnerIdOrderById(userId)).thenReturn(items);

        User booker = new User(2L, "booker@user.com", "booker");
        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setStartDate(LocalDateTime.of(2023,6,16,10,0,0));
        lastBooking.setEndDate(LocalDateTime.of(2023,6,17,10,0,0));
        lastBooking.setBooker(booker);
        lastBooking.setItem(item);
        lastBooking.setStatus(BookingStatus.APPROVED);

        List<Booking> lastBookings = List.of(lastBooking);
        when(bookingRepository.findByListLastBooker(userId)).thenReturn(lastBookings);

        Booking nextBooking = new Booking();
        nextBooking.setId(1L);
        nextBooking.setStartDate(LocalDateTime.of(2023,6,20,10,0,0));
        nextBooking.setEndDate(LocalDateTime.of(2023,6,21,10,0,0));
        nextBooking.setBooker(booker);
        nextBooking.setItem(item);
        nextBooking.setStatus(BookingStatus.APPROVED);

        List<Booking> nextBookings = List.of(nextBooking);
        when(bookingRepository.findByListNextBooker(userId)).thenReturn(nextBookings);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setCreated(LocalDateTime.of(2023,6,21,11,0,0));
        comment.setAuthor(booker);
        comment.setText("Add comment");
        comment.setItem(item);

        List<Comment> comments = List.of(comment);
        when(commentRepository.findByListItems(List.of(itemId))).thenReturn(comments);

        List<ItemDtoResponse> itemDtoResponse = itemService.getItems(userId, from, size);

        assertEquals(1, itemDtoResponse.size());
        assertEquals(BookingMapper.toBookingDto(lastBooking), itemDtoResponse.get(0).getLastBooking());
        assertEquals(BookingMapper.toBookingDto(nextBooking), itemDtoResponse.get(0).getNextBooking());
        assertEquals(1, itemDtoResponse.get(0).getComments().size());
        assertEquals(CommentMapper.toCommentDto(comment), itemDtoResponse.get(0).getComments().get(0));
    }

    @Test
    void getItemsFromSizeNotEmpty() {
        Long userId = 1L;

        Long itemId = 1L;
        Integer from = 0;
        Integer size = 1;

        Item item = new Item();
        item.setId(itemId);
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        item.setAvailable(true);
        item.setOwner(user);

        FromSizePageable page = FromSizePageable.of(from, size, Sort.by("id").descending());
        Page<Item> pg = new PageImpl<>(List.of(item));
        when(repository.findByOwnerId(userId, page)).thenReturn(pg);

        List<ItemDtoResponse> itemDtoResponse = itemService.getItems(userId, from, size);

        assertEquals(1, itemDtoResponse.size());
        assertNull(itemDtoResponse.get(0).getLastBooking());
        assertNull(itemDtoResponse.get(0).getNextBooking());
        assertEquals(0, itemDtoResponse.get(0).getComments().size());
    }

    @Test
    void getAvailableItems() {
        String text = "дРелЬ";
        Integer from = null;
        Integer size = null;

        Long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        item.setAvailable(true);
        item.setOwner(user);

        when(repository.findAvailableItemsByNameDescription(text)).thenReturn(List.of(item));

        List<ItemDto> itemDtoResponse = itemService.getAvailableItems(text, from, size);

        assertEquals(1, itemDtoResponse.size());
        assertEquals(itemId, itemDtoResponse.get(0).getId());
    }

    @Test
    void getAvailableItemsFromSizeNotEmpty() {
        String text = "дРелЬ";
        Integer from = 0;
        Integer size = 1;

        Long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        item.setAvailable(true);
        item.setOwner(user);

        FromSizePageable page = FromSizePageable.of(from, size, Sort.by("id").descending());
        Page<Item> pg = new PageImpl<>(List.of(item));
        when(repository.findAvailableItemsByNameDescription(text, page)).thenReturn(pg);

        List<ItemDto> itemDtoResponse = itemService.getAvailableItems(text, from, size);

        assertEquals(1, itemDtoResponse.size());
        assertEquals(itemId, itemDtoResponse.get(0).getId());
    }

    @Test
    void createComment() {
        Long userId = 1L;

        Long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        item.setAvailable(true);
        item.setOwner(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(repository.findById(itemId)).thenReturn(Optional.of(item));

        Booking booking = new Booking();
        when(bookingRepository.findByListOfBookings(itemId, userId)).thenReturn(List.of(booking));

        User author = new User(2L, "author@user.com", "author");
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("comment");
        comment.setItem(item);
        comment.setAuthor(author);

        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        CommentDto commentDtoResponse = itemService.createComment(commentDto, itemId, userId);

        assertEquals(CommentMapper.toCommentDto(comment), commentDtoResponse);
        verify(commentRepository).save(CommentMapper.toComment(commentDto, user));
    }
}