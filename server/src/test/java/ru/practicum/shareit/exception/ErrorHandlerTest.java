package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.model.BadRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ErrorHandlerTest {

    @Test
    void methodTests() {
        ErrorHandler errorHandler = new ErrorHandler();
        ConflictException conflictException = new ConflictException("Conflict");
        var response = errorHandler.handleConflictException(conflictException);
        assertThat(response.getError(), equalTo(conflictException.getMessage()));

        EntityNotFoundException entityNotFoundException = new EntityNotFoundException("404");
        var response2 = errorHandler.handleEntityNotFoundException(entityNotFoundException);
        assertThat(response2.getError(), equalTo(entityNotFoundException.getMessage()));

        Throwable throwable = new Throwable("throw");
        var response3 = errorHandler.handleThrowable(throwable);
        assertThat(response3.getError(), equalTo(throwable.getMessage()));

        BadRequest badRequest = new BadRequest("bad request");
        var response4 = errorHandler.badRequestException(badRequest);
        assertThat(response4.getError(), equalTo(badRequest.getMessage()));

    }
}
