package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository repository;


    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyBootstrappingByPersisting() {
        User user = User.builder().email("11234@mail.ru").name("tryHard").build();

        Assertions.assertNull(user.getId());
        em.persist(user);
        Assertions.assertNotNull(user.getId());

        Item item = Item.builder().available(true).owner(user).description("пустой предмет")
                .name("0").build();

        Assertions.assertNull(item.getId());
        em.persist(item);
        Assertions.assertNotNull(item.getId());

        LocalDateTime date = LocalDateTime.of(2024,10,10, 11, 11,11);
        Booking booking = Booking.builder().status(BookingStatus.APPROVED).start(date).end(date.plusDays(3))
                .booker(user).item(item).build();

        Assertions.assertNull(booking.getId());
        em.persist(booking);
        Assertions.assertNotNull(booking.getId());
    }

    @Test
    void verifyRepositoryByPersisting() {
        User user = User.builder().email("1234@mail.ru").name("76tryHard").build();

        Assertions.assertNull(user.getId());
        em.persist(user);
        Assertions.assertNotNull(user.getId());

        Item item = Item.builder().available(true).owner(user).description("пустой предмет")
                .name("0").build();

        Assertions.assertNull(item.getId());
        em.persist(item);
        Assertions.assertNotNull(item.getId());

        LocalDateTime date = LocalDateTime.of(2024,10,10, 11, 11,11);
        Booking booking = Booking.builder().status(BookingStatus.APPROVED).start(date).end(date.plusDays(3))
                .booker(user).item(item).build();

        Assertions.assertNull(booking.getId());
        repository.save(booking);
        Assertions.assertNotNull(booking.getId());
    }

    @Test
    void findByIdAndOwnerOrBooker() {
        User user = User.builder().email("1234@mail.ru").name("76tryHard").build();
        User booker = User.builder().email("booker1@mail.ru").name("booker1").build();
        Assertions.assertNull(user.getId());
        em.persist(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertNull(booker.getId());
        em.persist(booker);
        Assertions.assertNotNull(booker.getId());

        Item item = Item.builder().available(true).owner(user).description("пустой предмет")
                .name("0").build();

        Assertions.assertNull(item.getId());
        em.persist(item);
        Assertions.assertNotNull(item.getId());

        LocalDateTime date = LocalDateTime.of(2024,10,10, 11, 11,11);
        Booking booking = Booking.builder().status(BookingStatus.APPROVED).start(date).end(date.plusDays(3))
                .booker(booker).item(item).build();

        Assertions.assertNull(booking.getId());
        em.persist(booking);
        Assertions.assertNotNull(booking.getId());

        var bookingOutputOpt = repository.findByIdAndOwnerOrBooker(booking.getId(), user.getId());
        assertThat(bookingOutputOpt.isPresent(), equalTo(true));
        var bookingOutput = bookingOutputOpt.get();

        assertThat(bookingOutput.getId(), notNullValue());
        assertThat(bookingOutput.getBooker(), equalTo(booker));
        assertThat(bookingOutput.getItem(), equalTo(item));
        assertThat(bookingOutput.getStatus(), equalTo(booking.getStatus()));
        assertThat(bookingOutput.getStart(), equalTo(booking.getStart()));
        assertThat(bookingOutput.getEnd(), equalTo(booking.getEnd()));

        bookingOutputOpt = repository.findByIdAndOwnerOrBooker(booking.getId(), booker.getId());
        assertThat(bookingOutputOpt.isPresent(), equalTo(true));
        bookingOutput = bookingOutputOpt.get();

        assertThat(bookingOutput.getId(), notNullValue());
        assertThat(bookingOutput.getBooker(), equalTo(booker));
        assertThat(bookingOutput.getItem(), equalTo(item));
        assertThat(bookingOutput.getStatus(), equalTo(booking.getStatus()));
        assertThat(bookingOutput.getStart(), equalTo(booking.getStart()));
        assertThat(bookingOutput.getEnd(), equalTo(booking.getEnd()));

        User otherUser = User.builder().email("qwert@mail.ru").name("trewq").build();
        em.persist(otherUser);

        bookingOutputOpt = repository.findByIdAndOwnerOrBooker(booking.getId(), otherUser.getId());
        assertThat(bookingOutputOpt.isPresent(), equalTo(false));
    }

    @Test
    void findByIdAndOwnerId() {
        User user = User.builder().email("41234@mail.ru").name("476tryHard").build();
        User booker = User.builder().email("booker14@mail.ru").name("4booker1").build();
        Assertions.assertNull(user.getId());
        em.persist(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertNull(booker.getId());
        em.persist(booker);
        Assertions.assertNotNull(booker.getId());

        Item item = Item.builder().available(true).owner(user).description("пустой предмет")
                .name("4").build();

        Assertions.assertNull(item.getId());
        em.persist(item);
        Assertions.assertNotNull(item.getId());

        LocalDateTime date = LocalDateTime.of(2024,10,10, 11, 11,11);
        Booking booking = Booking.builder().status(BookingStatus.APPROVED).start(date).end(date.plusDays(3))
                .booker(booker).item(item).build();

        Assertions.assertNull(booking.getId());
        em.persist(booking);
        Assertions.assertNotNull(booking.getId());

        var bookingOutputOpt = repository.findByIdAndOwnerId(booking.getId(), user.getId());
        assertThat(bookingOutputOpt.isPresent(), equalTo(true));
        var bookingOutput = bookingOutputOpt.get();

        assertThat(bookingOutput.getId(), notNullValue());
        assertThat(bookingOutput.getBooker(), equalTo(booker));
        assertThat(bookingOutput.getItem(), equalTo(item));
        assertThat(bookingOutput.getStatus(), equalTo(booking.getStatus()));
        assertThat(bookingOutput.getStart(), equalTo(booking.getStart()));
        assertThat(bookingOutput.getEnd(), equalTo(booking.getEnd()));

        bookingOutputOpt = repository.findByIdAndOwnerId(booking.getId(), booker.getId());
        assertThat(bookingOutputOpt.isPresent(), equalTo(false));

        User otherUser = User.builder().email("qwerty@mail.ru").name("ytrewq").build();
        em.persist(otherUser);

        bookingOutputOpt = repository.findByIdAndOwnerId(booking.getId(), otherUser.getId());
        assertThat(bookingOutputOpt.isPresent(), equalTo(false));
    }

    @Test
    void findFirstByItemIdAndStartBeforeOrderByStartDesc() {
        User user = User.builder().email("541234@mail.ru").name("5476tryHard").build();
        User booker = User.builder().email("booker145@mail.ru").name("54booker1").build();
        Assertions.assertNull(user.getId());
        em.persist(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertNull(booker.getId());
        em.persist(booker);
        Assertions.assertNotNull(booker.getId());

        Item item = Item.builder().available(true).owner(user).description("пустой предмет")
                .name("45").build();

        Assertions.assertNull(item.getId());
        em.persist(item);
        Assertions.assertNotNull(item.getId());

        LocalDateTime date = LocalDateTime.of(2024,10,10, 11, 11,11);
        Booking booking = Booking.builder().status(BookingStatus.APPROVED).start(date).end(date.plusDays(3))
                .booker(booker).item(item).build();

        Assertions.assertNull(booking.getId());
        em.persist(booking);
        Assertions.assertNotNull(booking.getId());

        Booking booking2 = Booking.builder().status(BookingStatus.APPROVED).start(date.minusDays(2)).end(date.plusDays(2))
                .booker(booker).item(item).build();

        Assertions.assertNull(booking2.getId());
        em.persist(booking2);
        Assertions.assertNotNull(booking2.getId());

        var bookingOutput = repository.findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(), date.plusDays(1));
        assertThat(bookingOutput, notNullValue());

        assertThat(bookingOutput.getId(), equalTo(booking.getId()));
        assertThat(bookingOutput.getBooker(), equalTo(booker));
        assertThat(bookingOutput.getItem(), equalTo(item));
        assertThat(bookingOutput.getStatus(), equalTo(booking.getStatus()));
        assertThat(bookingOutput.getStart(), equalTo(booking.getStart()));
        assertThat(bookingOutput.getEnd(), equalTo(booking.getEnd()));
    }

    @Test
    void findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc() {
        User user = User.builder().email("541234@mail.ru").name("5476tryHard").build();
        User booker = User.builder().email("booker145@mail.ru").name("54booker1").build();
        Assertions.assertNull(user.getId());
        em.persist(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertNull(booker.getId());
        em.persist(booker);
        Assertions.assertNotNull(booker.getId());

        Item item = Item.builder().available(true).owner(user).description("пустой предмет")
                .name("45").build();

        Assertions.assertNull(item.getId());
        em.persist(item);
        Assertions.assertNotNull(item.getId());

        LocalDateTime date = LocalDateTime.of(2024,10,10, 11, 11,11);
        Booking booking = Booking.builder().status(BookingStatus.APPROVED).start(date.plusDays(2)).end(date.plusDays(3))
                .booker(booker).item(item).build();

        Assertions.assertNull(booking.getId());
        em.persist(booking);
        Assertions.assertNotNull(booking.getId());

        Booking booking2 = Booking.builder().status(BookingStatus.APPROVED).start(date.minusDays(2)).end(date.plusDays(2))
                .booker(booker).item(item).build();

        Assertions.assertNull(booking2.getId());
        em.persist(booking2);
        Assertions.assertNotNull(booking2.getId());

        var bookingOutput = repository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(item.getId(), date.plusDays(1), BookingStatus.APPROVED);
        assertThat(bookingOutput, notNullValue());

        assertThat(bookingOutput.getId(), equalTo(booking.getId()));
        assertThat(bookingOutput.getBooker(), equalTo(booker));
        assertThat(bookingOutput.getItem(), equalTo(item));
        assertThat(bookingOutput.getStatus(), equalTo(booking.getStatus()));
        assertThat(bookingOutput.getStart(), equalTo(booking.getStart()));
        assertThat(bookingOutput.getEnd(), equalTo(booking.getEnd()));
    }

}
