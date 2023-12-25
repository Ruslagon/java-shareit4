package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemInfo;
import ru.practicum.shareit.item.model.ItemInfoRequest;
import ru.practicum.shareit.request.model.Request;

@UtilityClass
public class DtoItemMapper {

    public static Item dtoToItem(ItemDto itemDto, Request request) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setName(itemDto.getName());
        item.setOwner(itemDto.getOwner());
        item.setRequest(request);
        return item;
    }

    public static ItemDto itemToDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        if (item.getRequest() == null) {
            itemDto.setRequestId(null);
        } else {
            itemDto.setRequestId(item.getRequest().getId());
        }
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setName(item.getName());
        itemDto.setOwner(item.getOwner());
        return itemDto;
    }

    public static ItemDto itemToDtoWithoutUsers(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        if (item.getRequest() == null) {
            itemDto.setRequestId(null);
        } else {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }

    public static ItemInfoDto itemInfoToDtoWithoutUsers(ItemInfo itemInfo) {
        ItemInfoDto itemDto = new ItemInfoDto();
        itemDto.setId(itemInfo.getId());
        itemDto.setName(itemInfo.getName());
        itemDto.setDescription(itemInfo.getDescription());
        itemDto.setAvailable(itemInfo.getAvailable());
        if (itemInfo.getRequest() != null) {
            itemDto.setRequestId(itemInfo.getRequest().getId());
        }
        itemDto.setComments(itemInfo.getComments());
        return itemDto;
    }

    public static Item updateItemFromDto(ItemDto itemDto, Item item) {
        if (itemDto == null) {
            return item;
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return item;
    }

    public static ItemInfoRequest itemForRequest(Item item) {
        return ItemInfoRequest.builder()
                .id(item.getId())
                .requestId(item.getRequest().getId())
                .description(item.getDescription())
                .name(item.getName())
                .available(item.getAvailable()).build();
    }
}
