package ru.practicum.shareit.request.model;

import ru.practicum.shareit.item.model.ItemInfoRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface RequestWithItems {

    Long getId();

    String getDescription();

    LocalDateTime getCreated();

    List<ItemInfoRequest> getItems();
}
