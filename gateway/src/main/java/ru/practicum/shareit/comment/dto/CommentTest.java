package ru.practicum.shareit.comment.dto;

import org.springframework.beans.factory.annotation.Value;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public interface CommentTest {

    Long getId();

    String getText();

    LocalDateTime getCreated();

    @Value("#{target.author.name}")
    String getAuthorName();

    void setId(Long id);

    void setText(String text);

    void setCreated(LocalDateTime created);

    void setAuthorName(String authorName);

    void setAuthor(User author);
}
