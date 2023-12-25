package ru.practicum.shareit.booking;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.DtoBookingMapper;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.model.BadRequest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingService {
    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingRepository bookingRepository;

    @Transactional
    public BookingInfoDto add(Long userId, BookingDto bookingDto) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user по id - " + userId + " не найден"));
        var item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("item по id - " + bookingDto.getItemId() + " не найден"));

        if (!item.getAvailable()) {
            throw new BadRequest("предмет заблокирован. itemId - " + item.getId());
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("user по id - " + userId + " не найден");
        }

        var booking = DtoBookingMapper.dtoToBooking(bookingDto, item, user);
        return DtoBookingMapper.bookingToDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingInfoDto update(Long userId, Long bookingId, Boolean approved) {
        var booking = bookingRepository.findByIdAndOwnerId(bookingId, userId)
                .orElseThrow(() -> new EntityNotFoundException("user по id - " + userId + " не найден"));
        if (approved) {
            if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                throw new BadRequest("не нужный запрос");
            }
            booking.setStatus(BookingStatus.APPROVED);
        } else if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            booking.setStatus(BookingStatus.CANCELED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return DtoBookingMapper.bookingToDto(bookingRepository.save(booking));
    }

    @Transactional()
    public void delete(Long requesterId, Long bookingId) {
        bookingRepository.findByIdAndOwnerOrBooker(bookingId, requesterId)
                .orElseThrow(() -> new ConflictException("вы не владеете правом на удаление"));
        bookingRepository.deleteById(bookingId);
    }

    public List<BookingInfoDto> getBookersBookings(Long bookerId, String state, Integer from, Integer size) {
        userRepository.findById(bookerId).orElseThrow(() -> new EntityNotFoundException("user по id - " + bookerId + " не найден"));
        LocalDateTime now = LocalDateTime.now();
        QBooking booking = QBooking.booking;
        BooleanExpression byBookerId = booking.booker.id.eq(bookerId);
        var sort2 = new QSort(booking.end.desc());
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size, sort2);
        if (BookingState.ALL.name().equals(state)) {
            return bookingRepository.findAll(byBookerId, pageRequest).map(DtoBookingMapper::bookingToDto)
                    .getContent();
        } else if (BookingState.CURRENT.name().equals(state)) {
            BooleanExpression startBeforeNow = booking.start.before(now);
            BooleanExpression endAfterNow = booking.end.after(now);
            return bookingRepository.findAll(byBookerId.and(startBeforeNow.and(endAfterNow)), pageRequest)
                    .map(DtoBookingMapper::bookingToDto).getContent();
        } else if (BookingState.FUTURE.name().equals(state)) {
            Predicate startAfterNow = booking.start.after(now);
            return bookingRepository.findAll(byBookerId.and(startAfterNow), pageRequest).map(DtoBookingMapper::bookingToDto)
                    .getContent();
        } else if (BookingState.PAST.name().equals(state)) {
            BooleanExpression endBeforeNow = booking.end.before(now);
            return bookingRepository.findAll(byBookerId.and(endBeforeNow), pageRequest).map(DtoBookingMapper::bookingToDto)
                    .getContent();
        } else if (BookingState.REJECTED.name().equals(state)) {
            BooleanExpression statusIsRejected = booking.status.eq(BookingStatus.REJECTED).or(booking.status.eq(BookingStatus.CANCELED));
            return bookingRepository.findAll(byBookerId.and(statusIsRejected), pageRequest).map(DtoBookingMapper::bookingToDto)
                    .getContent();
        } else if (BookingState.WAITING.name().equals(state)) {
            BooleanExpression statusIsWaiting = booking.status.eq(BookingStatus.WAITING);
            return bookingRepository.findAll(byBookerId.and(statusIsWaiting), pageRequest).map(DtoBookingMapper::bookingToDto)
                    .getContent();
        }
        throw new BadRequest("Unknown state: " + state);
    }

    public BookingInfoDto getOne(Long userId, Long bookingId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("отсутствует user id=" + userId));
        return DtoBookingMapper.bookingToDto(bookingRepository.findByIdAndOwnerOrBooker(bookingId, userId)
                .orElseThrow(() -> new EntityNotFoundException("аренда не доступна " + bookingId)));
    }

    public List<BookingInfoDto> getOwnersBookings(Long ownerId, String state, Integer from, Integer size) {
        System.out.println(state);
        userRepository.findById(ownerId).orElseThrow(() -> new EntityNotFoundException("user по id - " + ownerId + " не найден"));
        LocalDateTime now = LocalDateTime.now();
        QBooking booking = QBooking.booking;
        BooleanExpression byOwnerId = booking.item.owner.id.eq(ownerId);
        var sort = new QSort(booking.end.desc());
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size, sort);
        if (BookingState.ALL.name().equals(state)) {
            return bookingRepository.findAll(byOwnerId, pageRequest).map(DtoBookingMapper::bookingToDto)
                    .getContent();
        } else if (BookingState.CURRENT.name().equals(state)) {
            BooleanExpression startBeforeNow = booking.start.before(now);
            BooleanExpression endAfterNow = booking.end.after(now);
            return bookingRepository.findAll(byOwnerId.and(startBeforeNow.and(endAfterNow)), pageRequest)
                    .map(DtoBookingMapper::bookingToDto).getContent();
        } else if (BookingState.FUTURE.name().equals(state)) {
            Predicate startAfterNow = booking.start.after(now);
            return bookingRepository.findAll(byOwnerId.and(startAfterNow), pageRequest).map(DtoBookingMapper::bookingToDto)
                    .getContent();
        } else if (BookingState.PAST.name().equals(state)) {
            BooleanExpression endBeforeNow = booking.end.before(now);
            return bookingRepository.findAll(byOwnerId.and(endBeforeNow), pageRequest).map(DtoBookingMapper::bookingToDto)
                    .getContent();
        } else if (BookingState.REJECTED.name().equals(state)) {
            BooleanExpression statusIsRejected = booking.status.eq(BookingStatus.REJECTED).or(booking.status.eq(BookingStatus.CANCELED));
            return bookingRepository.findAll(byOwnerId.and(statusIsRejected), pageRequest).map(DtoBookingMapper::bookingToDto)
                    .getContent();
        } else if (BookingState.WAITING.name().equals(state)) {
            BooleanExpression statusIsWaiting = booking.status.eq(BookingStatus.WAITING);
            return bookingRepository.findAll(byOwnerId.and(statusIsWaiting), pageRequest).map(DtoBookingMapper::bookingToDto)
                    .getContent();
        }
        throw new BadRequest("Unknown state: " + state);
    }
}
