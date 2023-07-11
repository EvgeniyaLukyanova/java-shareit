package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constants.Constants.REQUEST_HEADER_FOR_USER;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody RequestDto request,
                                                @RequestHeader(REQUEST_HEADER_FOR_USER) Long userId) {
        log.info("Добавляем запрос: {}", request);
        return requestClient.createRequest(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsUser(@RequestHeader(REQUEST_HEADER_FOR_USER) Long userId) {
        log.info("Получение списка всех запросов пользователя с ид {}", userId);
        return requestClient.getRequestsUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                 @RequestHeader(REQUEST_HEADER_FOR_USER) Long userId) {
        log.info("Получение списка запросов, созданных другими пользователями");
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable Long id,
                                              @RequestHeader(REQUEST_HEADER_FOR_USER) Long userId) {
        log.info("Получение запроса с ид {}", id);
        return requestClient.getRequestById(userId, id);
    }

}
