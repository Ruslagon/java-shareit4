package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.QRequest;

public class QRequestTest {

    QRequest qRequest = QRequest.request;

    @Test
    void qTest() {
        qRequest.id.eq(1L);
        qRequest.created.asc();
        qRequest.user.id.eq(1L);
    }
}
