package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.DtoItemMapper;
import ru.practicum.shareit.item.model.ItemInfoRequest;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class DtoRequestMapper {
    public static Request dtoToRequest(RequestDto requestDto, User user) {
        Request request = Request.builder()
                .user(user)
                .description(requestDto.getDescription())
                .created(LocalDateTime.now()).build();
        return request;
    }

    public static RequestDto requestToDto(Request request) {
        RequestDto requestDto = RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .description(request.getDescription()).build();
        return requestDto;
    }

    public static RequestDto requestToDtoWithItems(Request request) {
        RequestDto requestDto = RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .description(request.getDescription())
                .items(getRequestItem(request))
                .build();
        return requestDto;
    }

    List<ItemInfoRequest> getRequestItem(Request request) {
        if (request.getItems() == null) {
            return new ArrayList<>();
        } else {
            return request.getItems().stream().map(DtoItemMapper::itemForRequest).collect(Collectors.toList());
        }
    }
}
