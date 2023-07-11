package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;

import java.util.List;

public interface RequestService {
    RequestDto createRequest(RequestDto request, Long userId);

    List<RequestDtoResponse> getRequests(Long userId);

    List<RequestDtoResponse> getAllRequests(Integer from, Integer size, Long userId);

    public RequestDtoResponse getRequestById(Long id, Long userId);
}
