package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.model.ItemInfoRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class RequestDto {

    private Long id;

    @NotNull
    @NotBlank
    private String description;

    private LocalDateTime created;

    private List<ItemInfoRequest> items;
}
