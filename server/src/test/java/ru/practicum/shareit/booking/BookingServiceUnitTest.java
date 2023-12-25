package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.model.BadRequest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class BookingServiceUnitTest {

    private ItemRepository itemRepository = Mockito.mock(ItemRepository.class);

    private UserRepository userRepository = Mockito.mock(UserRepository.class);

    private BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);

    private BookingService bookingService = new BookingService(userRepository, itemRepository, bookingRepository);

    @Test
    void update() {
        User owner = User.builder().id(1L).email("1234@mail.ru").name("76tryHard").build();
        User booker = User.builder().id(2L).email("booker1@mail.ru").name("booker1").build();


        Item item = Item.builder().id(1L).available(true).owner(owner).description("пустой предмет")
                .name("0").build();

        LocalDateTime date = LocalDateTime.of(2024,10,10, 11, 11,11);
        Booking booking = Booking.builder().status(BookingStatus.WAITING).start(date).end(date.plusDays(3))
                .item(item).booker(booker).build();

        Mockito.when(bookingRepository.findByIdAndOwnerId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        Mockito.when(bookingRepository.save(Mockito.any()))
                .thenReturn(booking);

        var bookingInfoDto = bookingService.update(1L, 1L, true);

        assertThat(bookingInfoDto.getId(), equalTo(booking.getId()));
        assertThat(bookingInfoDto.getBooker().getId(), equalTo(booking.getBooker().getId()));
        assertThat(bookingInfoDto.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(bookingInfoDto.getStatus(), equalTo(BookingStatus.APPROVED));
        assertThat(bookingInfoDto.getStart(), equalTo(booking.getStart()));
        assertThat(bookingInfoDto.getEnd(), equalTo(booking.getEnd()));

        booking.setStatus(BookingStatus.WAITING);
        bookingInfoDto = bookingService.update(1L, 1L, false);

        assertThat(bookingInfoDto.getId(), equalTo(booking.getId()));
        assertThat(bookingInfoDto.getBooker().getId(), equalTo(booking.getBooker().getId()));
        assertThat(bookingInfoDto.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(bookingInfoDto.getStart(), equalTo(booking.getStart()));
        assertThat(bookingInfoDto.getEnd(), equalTo(booking.getEnd()));

        var booking2 = booking;
        booking2.setStatus(BookingStatus.APPROVED);
        Mockito.when(bookingRepository.findByIdAndOwnerId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(booking2));
        assertThrows(BadRequest.class, () -> bookingService.update(1L,1L, true));

        bookingInfoDto = bookingService.update(1L, 1L, false);

        assertThat(bookingInfoDto.getId(), equalTo(booking2.getId()));
        assertThat(bookingInfoDto.getBooker().getId(), equalTo(booking2.getBooker().getId()));
        assertThat(bookingInfoDto.getItem().getId(), equalTo(booking2.getItem().getId()));
        assertThat(bookingInfoDto.getStart(), equalTo(booking2.getStart()));
        assertThat(bookingInfoDto.getEnd(), equalTo(booking2.getEnd()));
    }

    @Test
    void getOne() {
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(User.builder().build()));

        LocalDateTime date = LocalDateTime.of(2023,10,10, 11, 11,11);

        Booking lastBooking = Booking.builder().id(1L).start(date.minusDays(4))
                .end(date.minusDays(3)).item(Item.builder().id(1L).build()).booker(User.builder().id(1L).build())
                .status(BookingStatus.APPROVED).build();

        Mockito.when(bookingRepository.findByIdAndOwnerOrBooker(Mockito.anyLong(), Mockito.any()))
                .thenReturn(Optional.of(lastBooking));

        var booking = bookingService.getOne(1L, 1L);

        assertThat(booking.getId(), equalTo(lastBooking.getId()));
        assertThat(booking.getStatus(), equalTo(lastBooking.getStatus()));
        assertThat(booking.getStart(), equalTo(lastBooking.getStart()));
        assertThat(booking.getEnd(), equalTo(lastBooking.getEnd()));
        assertThat(booking.getItem().getId(), equalTo(lastBooking.getItem().getId()));
        assertThat(booking.getBooker().getId(), equalTo(lastBooking.getBooker().getId()));
    }
}
