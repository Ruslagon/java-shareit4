package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.ItemBookingsInfo;
import ru.practicum.shareit.user.model.UserInfoId;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingInfoDto {
    private Long id;
    private Long itemId;
    private ItemBookingsInfo item;
    private UserInfoId booker;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
}
