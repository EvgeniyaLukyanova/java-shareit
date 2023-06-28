package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    @Mock
    RequestRepository repository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    private RequestService requestService;
    private User user;
    private Item item;
    private Request request;

    @BeforeEach
    void setUp() {
        requestService = new RequestServiceImpl(repository,
                userRepository,
                itemRepository);

        user = new User(
                1L,
                "user@user.com",
                "user");

        User owner = new User(
                2L,
                "owner@user.com",
                "owner");

        request = new Request(1L,
                "Хотел бы воспользоваться щёткой для обуви",
                user,
                LocalDateTime.of(2023,6,21,11,0,0));

        item = new Item(1L,
                "Аккумуляторная дрель",
                "Аккумуляторная дрель + аккумулятор",
                true,
                owner,
                request);
    }

    @Test
    void createBooking() {
        Long userId = 1L;
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        when(repository.save(any())).thenReturn(request);

        RequestDto requestDto = new RequestDto();
        requestDto.setDescription("Хотел бы воспользоваться щёткой для обуви");

        RequestDto requestDtoResponse = requestService.createRequest(requestDto, userId);

        assertEquals(requestDto.getDescription(), requestDtoResponse.getDescription());
        assertEquals(UserMapper.toUserDto(request.getRequestor()), requestDtoResponse.getRequestor());
        assertEquals(request.getCreated(), requestDtoResponse.getCreated());

        verify(repository).save(RequestMapper.toRequest(requestDto));
    }

    @Test
    void getRequests() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(repository.findByRequestorIdOrderByCreatedDesc(userId)).thenReturn(List.of(request));

        when(itemRepository.findByRequestIdInOrderById(List.of(request.getId()))).thenReturn(List.of(item));

        List<RequestDtoResponse> requestDtoResponses = requestService.getRequests(userId);

        assertEquals(1, requestDtoResponses.size());
        assertEquals(request.getDescription(), requestDtoResponses.get(0).getDescription());
        assertEquals(1, requestDtoResponses.get(0).getItems().size());
        assertEquals(item.getName(), requestDtoResponses.get(0).getItems().get(0).getName());
    }

    @Test
    void getAllRequests() {
        Long userId = 1L;
        Integer from = null;
        Integer size = null;

        when(repository.findAllByRequestorIdNotOrderByCreatedDesc(userId)).thenReturn(List.of(request));

        when(itemRepository.findByRequestIdInOrderById(List.of(request.getId()))).thenReturn(List.of(item));

        List<RequestDtoResponse> requestDtoResponses = requestService.getAllRequests(from, size, userId);

        assertEquals(1, requestDtoResponses.size());
        assertEquals(request.getDescription(), requestDtoResponses.get(0).getDescription());
        assertEquals(1, requestDtoResponses.get(0).getItems().size());
        assertEquals(item.getName(), requestDtoResponses.get(0).getItems().get(0).getName());
    }

    @Test
    void getAllRequests_fromSizeNotEmpty() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 1;

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created").descending());
        Page<Request> pg = new PageImpl<>(List.of(request));
        when(repository.findAllByRequestorIdNot(userId, page)).thenReturn(pg);

        when(itemRepository.findByRequestIdInOrderById(List.of(request.getId()))).thenReturn(List.of(item));

        List<RequestDtoResponse> requestDtoResponses = requestService.getAllRequests(from, size, userId);

        assertEquals(1, requestDtoResponses.size());
        assertEquals(request.getDescription(), requestDtoResponses.get(0).getDescription());
        assertEquals(1, requestDtoResponses.get(0).getItems().size());
        assertEquals(item.getName(), requestDtoResponses.get(0).getItems().get(0).getName());
    }

    @Test
    void getAllRequests_SizeEmpty() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = null;

        Throwable exception = assertThrows(ValidationException.class, () -> requestService.getAllRequests(from, size, userId));
        assertEquals("Должны быть заполненны оба параметра: from, size", exception.getMessage());
    }

    @Test
    void getAllRequests_fromLessThanZero() {
        Long userId = 1L;
        Integer from = -1;
        Integer size = 1;

        Throwable exception = assertThrows(ValidationException.class, () -> requestService.getAllRequests(from, size, userId));
        assertEquals("Не верное значение параметра from", exception.getMessage());
    }

    @Test
    void getAllRequests_fromLessThanOne() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 0;

        Throwable exception = assertThrows(ValidationException.class, () -> requestService.getAllRequests(from, size, userId));
        assertEquals("Не верное значение параметра size", exception.getMessage());
    }

    @Test
    void getRequestById() {
        Long userId = 1L;
        Long requestId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(repository.findById(requestId)).thenReturn(Optional.of(request));

        when(itemRepository.findByRequestIdInOrderById(List.of(request.getId()))).thenReturn(List.of(item));

        RequestDtoResponse requestDtoResponse = requestService.getRequestById(requestId, userId);
        assertEquals(request.getDescription(), requestDtoResponse.getDescription());
        assertEquals(item.getName(), requestDtoResponse.getItems().get(0).getName());
    }
}