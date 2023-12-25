package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingInfoDto add(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                              @RequestBody BookingDto bookingDto) {
        log.info("добавить заказ для пользователя userId={} и предмета itemId={}", bookerId, bookingDto.getItemId());
        log.info("booking для добавления={}", bookingDto);
        return bookingService.add(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingInfoDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable Long bookingId,
                           @RequestParam Boolean approved) {
        log.info("изменить для пользователя userId={} данные bookingId={}", ownerId, bookingId);
        log.info("данные для изменения={}", approved);
        return bookingService.update(ownerId, bookingId, approved);
    }

    @DeleteMapping("/{bookingId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") Long requesterId, @PathVariable Long bookingId) {
        log.info("удалить заказ с id={}", requesterId);
        bookingService.delete(requesterId, bookingId);
    }

    @GetMapping
    public List<BookingInfoDto> getUsersBookings(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                              @RequestParam String state,
                                                 @RequestParam Integer from,
                                                 @RequestParam Integer size) {
        log.info("получить заказы по заказчику={}", bookerId);
        return bookingService.getBookersBookings(bookerId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public BookingInfoDto getOne(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.info("получить заказ для bookingId={}",bookingId);
        return bookingService.getOne(userId, bookingId);
    }

    @GetMapping("/owner")
    public List<BookingInfoDto> getOwnersBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @RequestParam String state,
                                                  @RequestParam Integer from,
                                                  @RequestParam Integer size) {
        log.info("получить заказы по владельцу={}", ownerId);
        return bookingService.getOwnersBookings(ownerId, state, from, size);
    }
}
