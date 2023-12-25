package ru.practicum.shareit.comment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CommentDto {
    @EqualsAndHashCode.Include
    Long id;
    @NotBlank
    String text;
    LocalDateTime created = LocalDateTime.now();
    String authorName;

}
