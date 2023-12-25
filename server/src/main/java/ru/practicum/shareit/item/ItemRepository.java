package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemInfo;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select it " +
            "from Item as it " +
            "JOIN FETCH it.owner as ow " +
            "where it.id = ?1 " +
            "and ow.id = ?2 ")
    Optional<Item> findByIdAndWithUser(Long itemId, Long userId);

    Page<Item> findAllByOwnerId(Long userId, PageRequest pageRequest);

    Page<Item> findAllByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingAndAvailableTrue(String text, String sameText, PageRequest pageRequest);

    @Query("select it " +
            "from Item as it " +
            "JOIN it.bookings as booking " +
            "JOIN booking.booker as booker " +
            "where it.id = ?1 " +
            "and booker.id = ?2 " +
            "and booking.status = ?3 " +
            "and booking.end < ?4 ")
    Optional<Item> findByIdAndHaveBookingsByUserId(Long itemId, Long userId, BookingStatus status, LocalDateTime now);

    Optional<ItemInfo> findByOwnerIdAndId(Long userId, Long itemId);

    @Query("select it " +
            "from Item as it " +
            "where it.id = ?1 ")
    Optional<ItemInfo> findByIdWithComments(Long id);
}
