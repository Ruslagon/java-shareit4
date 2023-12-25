package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;

public class RequestServiceUnitTest {

    UserRepository userRepository = Mockito.mock(UserRepository.class);

    RequestRepository requestRepository = Mockito.mock(RequestRepository.class);

    private LocalDateTime date = LocalDateTime.of(2023,10,10, 11, 11,11);

    private RequestService requestService = new RequestService(requestRepository, userRepository);

    @Test
    void testCreateRequestWithMock() throws SQLException {
        User user = User.builder().email("1234@mail.ru").name("tryHard").id(1L).build();

        RequestDto requestDto = RequestDto.builder().description("хочу спать").build();
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(requestRepository.save(Mockito.any(Request.class)))
                .thenReturn(Request.builder().created(date)
                        .description(requestDto.getDescription())
                        .id(1L)
                        .user(user)
                        .build());

        var requestDtoReturn = requestService.add(1L, requestDto);

        assertThat(requestDtoReturn.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(requestDtoReturn.getId(), equalTo(1L));
        assertThat(requestDtoReturn.getCreated(), equalTo(date));
    }

    @Test
    void testGetRequestWithMock() throws SQLException {
        User user = User.builder().email("1234@mail.ru").name("tryHard").id(1L).build();

        RequestDto requestDto = RequestDto.builder().description("хочу спать").build();
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(Request.builder().created(date)
                        .description(requestDto.getDescription())
                        .id(1L)
                        .user(user)
                        .build()));

        var requestDtoReturn = requestService.getOne(1L, 1L);


        assertThat(requestDtoReturn.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(requestDtoReturn.getId(), equalTo(1L));
        assertThat(requestDtoReturn.getCreated(), equalTo(date));
    }
}
