package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConflictExceptionTest {

    @Test
    void conflictExceptionThrow() {
        Assertions.assertThrows(ConflictException.class, () -> {
            throw new ConflictException("ConflictException");
        });
    }
}
