package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.model.BadRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    private final EntityManager em;

    private final BookingService service;

    @Test
    void saveBooking() {
        User owner = User.builder().email("1234@mail.ru").name("76tryHard").build();
        User booker = User.builder().email("booker1@mail.ru").name("booker1").build();

        Assertions.assertNull(owner.getId());
        em.persist(owner);
        Assertions.assertNotNull(owner.getId());
        em.persist(booker);

        Item item = Item.builder().available(true).owner(owner).description("пустой предмет")
                .name("0").build();

        Assertions.assertNull(item.getId());
        em.persist(item);
        Assertions.assertNotNull(item.getId());

        LocalDateTime date = LocalDateTime.of(2024,10,10, 11, 11,11);
        BookingDto bookingDto = BookingDto.builder().status(BookingStatus.APPROVED).start(date).end(date.plusDays(3))
                .itemId(item.getId()).build();

        var bookingInfoDto = service.add(booker.getId(), bookingDto);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.end = :end", Booking.class);
        Booking booking = query.setParameter("end", bookingDto.getEnd())
                .getSingleResult();

        assertThat(booking.getId(), equalTo(bookingInfoDto.getId()));
        assertThat(booking.getItem().getId(), equalTo(bookingInfoDto.getItem().getId()));
        assertThat(booking.getBooker().getId(), equalTo(booker.getId()));
        assertThat(booking.getStatus(), equalTo(bookingInfoDto.getStatus()));
        assertThat(booking.getStart(), equalTo(bookingInfoDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingInfoDto.getEnd()));

        assertThrows(EntityNotFoundException.class, () -> service.add(-1L, bookingDto));

        bookingDto.setItemId(-1L);
        assertThrows(EntityNotFoundException.class, () -> service.add(booker.getId(), bookingDto));

        bookingDto.setItemId(item.getId());
        bookingDto.setEnd(date.minusYears(4));
        bookingDto.setStart(date.plusYears(13));
        //assertThrows(BadRequest.class, () -> service.add(booker.getId(), bookingDto));

    }

    @Test
    void deleteBooking() {
        User owner = User.builder().email("12345@mail.ru").name("765tryHard").build();
        User booker = User.builder().email("5booker1@mail.ru").name("5booker").build();

        Assertions.assertNull(owner.getId());
        em.persist(owner);
        Assertions.assertNotNull(owner.getId());
        em.persist(booker);

        Item item = Item.builder().available(true).owner(owner).description("пустой предмет")
                .name("0").build();

        Assertions.assertNull(item.getId());
        em.persist(item);
        Assertions.assertNotNull(item.getId());

        LocalDateTime date = LocalDateTime.of(2024,10,10, 11, 11,11);
        Booking booking = Booking.builder().status(BookingStatus.APPROVED).start(date).end(date.plusDays(9))
                .item(item).booker(booker).build();

        Assertions.assertNull(booking.getId());
        em.persist(booking);
        Assertions.assertNotNull(booking.getId());

        service.delete(booker.getId(), booking.getId());

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.end = :end", Booking.class);
        var bookingEmpty = query.setParameter("end", booking.getEnd())
                .getResultList();

        assertThat(bookingEmpty.isEmpty(), equalTo(true));
    }

    @Test
    void getBookersBookings() {
        User owner = User.builder().email("123456@mail.ru").name("7656tryHard").build();
        User booker = User.builder().email("6booker1@mail.ru").name("6booker").build();

        Assertions.assertNull(owner.getId());
        em.persist(owner);
        Assertions.assertNotNull(owner.getId());
        em.persist(booker);

        Item item = Item.builder().available(true).owner(owner).description("пустой предмет")
                .name("0").build();

        Assertions.assertNull(item.getId());
        em.persist(item);
        Assertions.assertNotNull(item.getId());

        LocalDateTime date = LocalDateTime.of(2024,10,10, 11, 11,11);

        List<Booking> bookings = List.of(
                Booking.builder().status(BookingStatus.APPROVED).start(date).end(date.plusDays(10))
                        .item(item).booker(booker).build(),
                Booking.builder().status(BookingStatus.APPROVED).start(date).end(date.plusDays(11))
                        .item(item).booker(booker).build(),
                Booking.builder().status(BookingStatus.WAITING).start(date).end(date.plusDays(12))
                        .item(item).booker(booker).build(),
                Booking.builder().status(BookingStatus.REJECTED).start(date.minusYears(4)).end(date.plusDays(13))
                        .item(item).booker(booker).build(),
                Booking.builder().status(BookingStatus.REJECTED).start(date.minusYears(5)).end(date.plusDays(14).minusYears(5))
                        .item(item).booker(booker).build(),
                Booking.builder().status(BookingStatus.REJECTED).start(date.minusYears(2)).end(date.plusDays(15).minusYears(2))
                        .item(item).booker(booker).build()
        );

        for (Booking booking : bookings) {
            Assertions.assertNull(booking.getId());
            em.persist(booking);
            Assertions.assertNotNull(booking.getId());
        }



        var bookingsOutput = service.getBookersBookings(booker.getId(), BookingState.ALL.toString(), 0, 10);
        assertThat(bookingsOutput, hasSize(6));
        for (Booking booking : bookings) {
            assertThat(bookingsOutput, hasItem(allOf(
                    hasProperty("id", equalTo(booking.getId())),
                    hasProperty("status", equalTo(booking.getStatus())),
                    hasProperty("start", equalTo(booking.getStart())),
                    hasProperty("end", equalTo(booking.getEnd()))
            )));
        }

        bookingsOutput = service.getBookersBookings(booker.getId(), BookingState.WAITING.toString(), 0, 10);
        assertThat(bookingsOutput, hasSize(1));
        for (Booking booking : bookings.stream().filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                .collect(Collectors.toList())) {
            assertThat(bookingsOutput, hasItem(allOf(
                    hasProperty("id", equalTo(booking.getId())),
                    hasProperty("status", equalTo(booking.getStatus())),
                    hasProperty("start", equalTo(booking.getStart())),
                    hasProperty("end", equalTo(booking.getEnd()))
            )));
        }

        bookingsOutput = service.getBookersBookings(booker.getId(), BookingState.REJECTED.toString(), 0, 10);
        assertThat(bookingsOutput, hasSize(3));
        for (Booking booking : bookings.stream().filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                .collect(Collectors.toList())) {
            assertThat(bookingsOutput, hasItem(allOf(
                    hasProperty("id", equalTo(booking.getId())),
                    hasProperty("status", equalTo(booking.getStatus())),
                    hasProperty("start", equalTo(booking.getStart())),
                    hasProperty("end", equalTo(booking.getEnd()))
            )));
        }

        bookingsOutput = service.getBookersBookings(booker.getId(), BookingState.FUTURE.toString(), 0, 10);
        assertThat(bookingsOutput, hasSize(3));
        for (Booking booking : bookings.stream().filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList())) {
            assertThat(bookingsOutput, hasItem(allOf(
                    hasProperty("id", equalTo(booking.getId())),
                    hasProperty("status", equalTo(booking.getStatus())),
                    hasProperty("start", equalTo(booking.getStart())),
                    hasProperty("end", equalTo(booking.getEnd()))
            )));
        }

        bookingsOutput = service.getBookersBookings(booker.getId(), BookingState.PAST.toString(), 0, 10);
        assertThat(bookingsOutput, hasSize(2));
        for (Booking booking : bookings.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList())) {
            assertThat(bookingsOutput, hasItem(allOf(
                    hasProperty("id", equalTo(booking.getId())),
                    hasProperty("status", equalTo(booking.getStatus())),
                    hasProperty("start", equalTo(booking.getStart())),
                    hasProperty("end", equalTo(booking.getEnd()))
            )));
        }

        bookingsOutput = service.getBookersBookings(booker.getId(), BookingState.CURRENT.toString(), 0, 10);
        assertThat(bookingsOutput, hasSize(1));
        for (Booking booking : bookings.stream()
                .filter(booking -> (booking.getEnd().isAfter(LocalDateTime.now()) && booking.getStart().isBefore(LocalDateTime.now())))
                .collect(Collectors.toList())) {
            assertThat(bookingsOutput, hasItem(allOf(
                    hasProperty("id", equalTo(booking.getId())),
                    hasProperty("status", equalTo(booking.getStatus())),
                    hasProperty("start", equalTo(booking.getStart())),
                    hasProperty("end", equalTo(booking.getEnd()))
            )));
        }

        assertThrows(BadRequest.class, () -> service.getBookersBookings(booker.getId(), "unknown state", 0, 10));
    }

    @Test
    void getOwnersBookings() {
        User owner = User.builder().email("1234567@mail.ru").name("76567tryHard").build();
        User booker = User.builder().email("7booker7@mail.ru").name("7booker").build();

        Assertions.assertNull(owner.getId());
        em.persist(owner);
        Assertions.assertNotNull(owner.getId());
        em.persist(booker);

        Item item = Item.builder().available(true).owner(owner).description("пустой предмет")
                .name("0").build();

        Assertions.assertNull(item.getId());
        em.persist(item);
        Assertions.assertNotNull(item.getId());

        LocalDateTime date = LocalDateTime.of(2024,10,10, 11, 11,11);

        List<Booking> bookings = List.of(
                Booking.builder().status(BookingStatus.APPROVED).start(date).end(date.plusDays(10))
                        .item(item).booker(booker).build(),
                Booking.builder().status(BookingStatus.APPROVED).start(date).end(date.plusDays(11))
                        .item(item).booker(booker).build(),
                Booking.builder().status(BookingStatus.WAITING).start(date).end(date.plusDays(12))
                        .item(item).booker(booker).build(),
                Booking.builder().status(BookingStatus.REJECTED).start(date.minusYears(4)).end(date.plusDays(13))
                        .item(item).booker(booker).build(),
                Booking.builder().status(BookingStatus.REJECTED).start(date.minusYears(5)).end(date.plusDays(14).minusYears(5))
                        .item(item).booker(booker).build(),
                Booking.builder().status(BookingStatus.REJECTED).start(date.minusYears(2)).end(date.plusDays(15).minusYears(2))
                        .item(item).booker(booker).build()
        );

        for (Booking booking : bookings) {
            Assertions.assertNull(booking.getId());
            em.persist(booking);
            Assertions.assertNotNull(booking.getId());
        }

        var bookingsOutput = service.getOwnersBookings(owner.getId(), BookingState.ALL.toString(), 0, 10);
        assertThat(bookingsOutput, hasSize(6));
        for (Booking booking : bookings) {
            assertThat(bookingsOutput, hasItem(allOf(
                    hasProperty("id", equalTo(booking.getId())),
                    hasProperty("status", equalTo(booking.getStatus())),
                    hasProperty("start", equalTo(booking.getStart())),
                    hasProperty("end", equalTo(booking.getEnd()))
            )));
        }

        bookingsOutput = service.getOwnersBookings(owner.getId(), BookingState.WAITING.toString(), 0, 10);
        assertThat(bookingsOutput, hasSize(1));
        for (Booking booking : bookings.stream().filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                .collect(Collectors.toList())) {
            assertThat(bookingsOutput, hasItem(allOf(
                    hasProperty("id", equalTo(booking.getId())),
                    hasProperty("status", equalTo(booking.getStatus())),
                    hasProperty("start", equalTo(booking.getStart())),
                    hasProperty("end", equalTo(booking.getEnd()))
            )));
        }

        bookingsOutput = service.getOwnersBookings(owner.getId(), BookingState.REJECTED.toString(), 0, 10);
        assertThat(bookingsOutput, hasSize(3));
        for (Booking booking : bookings.stream().filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                .collect(Collectors.toList())) {
            assertThat(bookingsOutput, hasItem(allOf(
                    hasProperty("id", equalTo(booking.getId())),
                    hasProperty("status", equalTo(booking.getStatus())),
                    hasProperty("start", equalTo(booking.getStart())),
                    hasProperty("end", equalTo(booking.getEnd()))
            )));
        }

        bookingsOutput = service.getOwnersBookings(owner.getId(), BookingState.FUTURE.toString(), 0, 10);
        assertThat(bookingsOutput, hasSize(3));
        for (Booking booking : bookings.stream().filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList())) {
            assertThat(bookingsOutput, hasItem(allOf(
                    hasProperty("id", equalTo(booking.getId())),
                    hasProperty("status", equalTo(booking.getStatus())),
                    hasProperty("start", equalTo(booking.getStart())),
                    hasProperty("end", equalTo(booking.getEnd()))
            )));
        }

        bookingsOutput = service.getOwnersBookings(owner.getId(), BookingState.PAST.toString(), 0, 10);
        assertThat(bookingsOutput, hasSize(2));
        for (Booking booking : bookings.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList())) {
            assertThat(bookingsOutput, hasItem(allOf(
                    hasProperty("id", equalTo(booking.getId())),
                    hasProperty("status", equalTo(booking.getStatus())),
                    hasProperty("start", equalTo(booking.getStart())),
                    hasProperty("end", equalTo(booking.getEnd()))
            )));
        }

        bookingsOutput = service.getOwnersBookings(owner.getId(), BookingState.CURRENT.toString(), 0, 10);
        assertThat(bookingsOutput, hasSize(1));
        for (Booking booking : bookings.stream()
                .filter(booking -> (booking.getEnd().isAfter(LocalDateTime.now()) && booking.getStart().isBefore(LocalDateTime.now())))
                .collect(Collectors.toList())) {
            assertThat(bookingsOutput, hasItem(allOf(
                    hasProperty("id", equalTo(booking.getId())),
                    hasProperty("status", equalTo(booking.getStatus())),
                    hasProperty("start", equalTo(booking.getStart())),
                    hasProperty("end", equalTo(booking.getEnd()))
            )));
        }

        assertThrows(BadRequest.class, () -> service.getOwnersBookings(booker.getId(), "unknown state", 0, 10));
    }
}
