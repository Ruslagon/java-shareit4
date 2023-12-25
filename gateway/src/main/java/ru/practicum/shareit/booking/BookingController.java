package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.model.BadRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
											  @Valid @RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		checkBookingTime(requestDto);
		return bookingClient.bookItem(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnersBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
												  @Valid @RequestParam(name = "state", defaultValue = "all") String stateParam,
												  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
												  @RequestParam(defaultValue = "50") @Positive Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("получить заказы по владельцу={}", ownerId);
		return bookingClient.getOwnersBookings(ownerId, state, from, size);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable Long bookingId,
								 @RequestParam Boolean approved) {
		log.info("изменить для пользователя userId={} данные bookingId={}", ownerId, bookingId);
		log.info("данные для изменения={}", approved);
		return bookingClient.updateBooking(ownerId, bookingId, approved);
	}

	@DeleteMapping("/{bookingId}")
	public ResponseEntity<Object> deleteBooker(@RequestHeader("X-Sharer-User-Id") Long requesterId, @PathVariable Long bookingId) {
		log.info("удалить заказ с id={}", requesterId);
		return bookingClient.deleteBooker(requesterId, bookingId);
	}

	private void checkBookingTime(BookItemRequestDto booking) {
		if (booking.getStart().isAfter(booking.getEnd()) || booking.getStart().isEqual(booking.getEnd())) {
			throw new BadRequest("время введено неверно");
		}
	}
}
