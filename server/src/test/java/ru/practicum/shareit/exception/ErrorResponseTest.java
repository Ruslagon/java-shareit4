package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.model.ErrorResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ErrorResponseTest {

    @Test
    void getError() {
        String error = "erRor";
        ErrorResponse errorResponse = new ErrorResponse(error);
        assertThat(errorResponse.getError(), equalTo(error));
    }
}
