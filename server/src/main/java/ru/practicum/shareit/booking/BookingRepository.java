package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {
    @Query("select book " +
            "from Booking as book " +
            "JOIN FETCH book.booker as ber " +
            "JOIN FETCH book.item as it " +
            "JOIN it.owner as ow " +
            "where book.id = ?1 " +
            "and ber.id = ?2 " +
            "or book.id = ?1 " +
            "and ow.id = ?2 ")
    Optional<Booking> findByIdAndOwnerOrBooker(Long id, Long userId);

    @Query("select book " +
            "from Booking as book " +
            "JOIN FETCH book.booker as ber " +
            "JOIN FETCH book.item as it " +
            "JOIN it.owner as ow " +
            "where book.id = ?1 " +
            "and ow.id = ?2 ")
    Optional<Booking> findByIdAndOwnerId(Long id, Long userId);

    Booking findFirstByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime now);

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime now, BookingStatus status);
}
