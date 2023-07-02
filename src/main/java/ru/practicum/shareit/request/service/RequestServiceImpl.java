package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.pageable.FromSizePageable;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public RequestServiceImpl(RequestRepository repository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    @Override
    public RequestDto createRequest(RequestDto request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        request.setRequestor(UserMapper.toUserDto(user));
        request.setCreated(LocalDateTime.now());
        return RequestMapper.toRequestDto(repository.save(RequestMapper.toRequest(request)));
    }

    private Map<Long, List<Item>> getItemList(List<Request> requests) {
        List<Item> items = itemRepository.findByRequestIdInOrderById(requests.stream()
                .map(e -> e.getId())
                .collect(Collectors.toList()));
        Map<Long, List<Item>> mapRequestItems = new HashMap<>();
        for (Item item : items) {
            if (mapRequestItems.containsKey(item.getRequest().getId())) {
                mapRequestItems.get(item.getRequest().getId()).add(item);
            } else {
                List<Item> listItem = new ArrayList<>();
                listItem.add(item);
                mapRequestItems.put(item.getRequest().getId(), listItem);
            }
        }
        return mapRequestItems;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestDtoResponse> getRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        List<RequestDtoResponse> requestDtoResponses = new ArrayList<>();
        List<Request> requests = repository.findByRequestorIdOrderByCreatedDesc(userId);
        if (requests.size() != 0) {
            Map<Long, List<Item>> mapRequestItems = getItemList(requests);
            for (Request request : requests) {
                requestDtoResponses.add(RequestMapper.toRequestDtoResponce(request, mapRequestItems.get(request.getId())));
            }
        }
        return requestDtoResponses;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestDtoResponse> getAllRequests(Integer from, Integer size, Long userId) {
        List<Request> requests = new ArrayList<>();
        if (from != null & size != null) {
            FromSizePageable page = FromSizePageable.of(from, size, Sort.by("created").descending());
            requests = repository.findAllByRequestorIdNot(userId, page).stream().collect(Collectors.toList());
        } else {
            requests = repository.findAllByRequestorIdNotOrderByCreatedDesc(userId);
        }
        List<RequestDtoResponse> requestDtoResponses = new ArrayList<>();
        if (requests.size() != 0) {
            Map<Long, List<Item>> mapRequestItems = getItemList(requests);
            for (Request request : requests) {
                requestDtoResponses.add(RequestMapper.toRequestDtoResponce(request, mapRequestItems.get(request.getId())));
            }
        }
        return requestDtoResponses;
    }

    @Transactional(readOnly = true)
    @Override
    public RequestDtoResponse getRequestById(Long id, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        Request request = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с ид %s не найден", userId)));
        Map<Long, List<Item>> mapRequestItems = getItemList(List.of(request));
        return RequestMapper.toRequestDtoResponce(request, mapRequestItems.get(request.getId()));
    }
}
