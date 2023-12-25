package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@Validated
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
    @RequestBody ItemDto itemDto) {
        log.info("добавить для пользователя userId={}", userId);
        log.info("item для добавления={}", itemDto);
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto,
                       @PathVariable Long itemId) {
        log.info("изменить для пользователя userId={} данные itemId={}", userId, itemId);
        log.info("данные для изменения={}", itemDto);
        return itemService.update(userId, itemDto, itemId);
    }

    @GetMapping
    public List<ItemDto> getAllForUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam Integer from,
                                       @RequestParam Integer size) {
        log.info("получить все предметы для пользователя userId={}", userId);
        return itemService.getAllForUser(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemInfoDto getOne(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("получить предмет для itemId={}", itemId);
        return itemService.getOne(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam String text,
                                @RequestParam Integer from,
                                @RequestParam Integer size) {
        log.info("получить предметы по тексту={}", text);
        return itemService.search(userId, text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("удалить предмет с id={}", itemId);
        itemService.delete(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentTest addComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                  @RequestBody CommentDto commentDto) {
        log.info("добавить для предмета itemId={} комментарий от пользователя userId={}", itemId, userId);
        log.info("comment для добавления={}", commentDto);
        return itemService.addComment(userId, itemId, commentDto);
    }
}
