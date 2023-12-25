package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EntityNotFoundExceptionTest {

    @Test
    void entityNotFoundExceptionThrow() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            throw new EntityNotFoundException();
        });
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            throw new EntityNotFoundException("EntityNotFoundException");
        });
    }
}
