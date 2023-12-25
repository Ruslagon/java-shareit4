package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.model.QComment;

public class QCommentTest {
    QComment qComment = QComment.comment;

    @Test
    void qTest() {
        qComment.item.id.eq(1L);
        qComment.text.toLowerCase();
        qComment.id.eq(21L);

    }
}
