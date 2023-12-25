package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    RequestService requestService;

    @Autowired
    private MockMvc mvc;

    @Test
    void saveNewRequest() throws Exception {
        LocalDateTime date = LocalDateTime.of(2023,10,10, 11, 11,11);

        RequestDto requestDtoToStart = RequestDto.builder().description("Хотел бы воспользоваться щёткой для обуви")
                .build();

        RequestDto requestDto = RequestDto.builder().description("Хотел бы воспользоваться щёткой для обуви")
                .id(1L)
                .created(date)
                .build();

        when(requestService.add(any(), any()))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(requestDtoToStart))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(requestDto.getCreated().toString())));

        RequestDto requestDtoToStartExc = RequestDto.builder()
                .build();

//        mvc.perform(post("/requests")
//                        .header("X-Sharer-User-Id", "1")
//                        .content(mapper.writeValueAsString(requestDtoToStartExc))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.violations", notNullValue()));
    }

    @Test
    void getAllRequestsForUser() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(2020, 12,12,12,12,21,12);

        when(requestService.getAllForUser(anyLong(),anyInt(), anyInt()))
                .thenReturn(List.of(
                        RequestDto.builder().description("хочу спать").created(dateTime).id(1L).build(),
                        RequestDto.builder().description("хочу есть").created(dateTime.plusDays(10)).id(2L).build(),
                        RequestDto.builder().description("хочу отдохнуть").created(dateTime.plusDays(15)).id(3L).build()
                ));

//        mvc.perform(get("/requests")
//                        .header("X-Sharer-User-Id", "1")
//                        //.param("")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void getAllRequests() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(2020, 12,12,12,12,21,12);

        when(requestService.getAll(anyLong(),anyInt(), anyInt()))
                .thenReturn(List.of(
                        RequestDto.builder().description("хочу спать").created(dateTime).id(1L).build(),
                        RequestDto.builder().description("хочу есть").created(dateTime.plusDays(10)).id(2L).build(),
                        RequestDto.builder().description("хочу отдохнуть").created(dateTime.plusDays(15)).id(3L).build()
                ));

//        mvc.perform(get("/requests/all")
//                        .header("X-Sharer-User-Id", "1")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void getRequestById() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(2020, 12,12,12,12,21,12);

        RequestDto requestDto = RequestDto.builder().description("хочу спать").created(dateTime).id(1L).build();
        when(requestService.getOne(anyLong(),anyLong()))
                .thenReturn(requestDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(requestDto.getCreated().toString())));
    }
}
