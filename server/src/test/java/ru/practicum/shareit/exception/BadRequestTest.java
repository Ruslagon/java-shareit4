package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.model.BadRequest;

public class BadRequestTest {

    @Test
    void badRequestThrow() {
        Assertions.assertThrows(BadRequest.class, () -> {
            throw new BadRequest("bad request");
        });
    }
}
