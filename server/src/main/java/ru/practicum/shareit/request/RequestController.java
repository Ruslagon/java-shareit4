package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public RequestDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
    @RequestBody RequestDto requestDto) {
        log.info("добавить для пользователя userId={}", userId);
        log.info("item для добавления={}", requestDto);
        return requestService.add(userId, requestDto);
    }

    @GetMapping
    public List<RequestDto> getAllForUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam Integer from,
                                          @RequestParam Integer size) {
        log.info("получить все запросы пользователя userId={}", userId);
        return requestService.getAllForUser(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestDto getOne(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @PathVariable Long requestId) {
        log.info("получить запрос для requestId={}", requestId);
        return requestService.getOne(userId, requestId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam Integer from,
                                          @RequestParam Integer size) {
        log.info("получить все запросы через пользователя userId={}", userId);
        return requestService.getAll(userId, from, size);
    }
}
