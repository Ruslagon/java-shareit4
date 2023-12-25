package ru.practicum.shareit.item.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class ItemInfoRequest {
    Long id;

    String name;

    String description;

    Boolean available;

    Long requestId;
}
