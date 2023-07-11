package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.request.service.RequestService;
import java.util.Collection;

import static ru.practicum.shareit.constants.Constants.REQUEST_HEADER_FOR_USER;

@RestController
@RequestMapping("/requests")
@Slf4j
public class RequestController {
    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@RequestBody RequestDto request,
                                    @RequestHeader(REQUEST_HEADER_FOR_USER) Long userId) {
        log.info("Начинаем добавлять запрос: {}", request);
        RequestDto requestDto = requestService.createRequest(request, userId);
        log.info("Запрос добавлена: {}", request);
        return requestDto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<RequestDtoResponse> getRequests(@RequestHeader(REQUEST_HEADER_FOR_USER) Long userId) {
        log.info("Получение списка всех запросов пользователя с ид {}", userId);
        return requestService.getRequests(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public Collection<RequestDtoResponse> getAllRequests(@RequestParam(required = false) Integer from,
                                                         @RequestParam(required = false) Integer size,
                                                         @RequestHeader(REQUEST_HEADER_FOR_USER) Long userId) {
        log.info("Получение списка запросов, созданных другими пользователями");
        return requestService.getAllRequests(from, size, userId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RequestDtoResponse getRequestById(@PathVariable Long id,
                                             @RequestHeader(REQUEST_HEADER_FOR_USER) Long userId) {
        log.info("Получение запроса с ид {}", id);
        return requestService.getRequestById(id, userId);
    }
}
