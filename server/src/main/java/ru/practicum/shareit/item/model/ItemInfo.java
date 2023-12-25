package ru.practicum.shareit.item.model;

import ru.practicum.shareit.comment.dto.CommentTest;
import ru.practicum.shareit.request.model.RequestId;

import java.util.List;

public interface ItemInfo {

    Long getId();

    String getName();

    String getDescription();

    Boolean getAvailable();

//    @Value("#{target.request.id}")
//    Long getRequestId();

    RequestId getRequest();

    List<CommentTest> getComments();

    void setId(Long id);

    void setName(String name);

    void setDescription(String description);

    void setAvailable(Boolean available);

    void setRequest(RequestId requestId);

    void setComments(List<CommentTest> comments);
}
