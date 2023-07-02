package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class RequestMapper {
    public RequestDto toRequestDto(Request request) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setDescription(request.getDescription());
        requestDto.setRequestor(UserMapper.toUserDto(request.getRequestor()));
        requestDto.setCreated(request.getCreated());
        return requestDto;
    }

    public Request toRequest(RequestDto requestDto) {
        if (requestDto != null) {
            Request request = new Request();
            request.setId(requestDto.getId());
            request.setDescription(requestDto.getDescription());
            request.setRequestor(UserMapper.toUser(requestDto.getRequestor()));
            request.setCreated(requestDto.getCreated());
            return request;
        } else {
            return null;
        }
    }

    public RequestDtoResponse toRequestDtoResponce(Request request, List<Item> items) {
        RequestDtoResponse requestDto = new RequestDtoResponse();
        requestDto.setId(request.getId());
        requestDto.setDescription(request.getDescription());
        requestDto.setCreated(request.getCreated());
        if (items != null) {
            requestDto.setItems(items.stream().map(e -> ItemMapper.toItemDto(e)).collect(Collectors.toList()));
        } else {
            requestDto.setItems(new ArrayList<ItemDto>());
        }
        return requestDto;
    }
}
