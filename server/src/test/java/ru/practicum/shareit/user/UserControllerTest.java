package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    @Test
    void saveNewUser() throws Exception {
        UserDto userDtoToStart = UserDto.builder().name("johny").email("345@mail.ru").build();

        UserDto userDto = UserDto.builder().id(1L).name("johny").email("345@mail.ru").build();

        when(userService.add(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoToStart))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        UserDto userDtoToStartExc1 = UserDto.builder().email("345@mail.ru")
                .build();

//        mvc.perform(post("/users")
//                        .header("X-Sharer-User-Id", "1")
//                        .content(mapper.writeValueAsString(userDtoToStartExc1))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("violations", notNullValue()));

        UserDto userDtoToStartExc2 = UserDto.builder().name("harry").email("345ail.ru")
                .build();

//        mvc.perform(post("/users")
//                        .header("X-Sharer-User-Id", "1")
//                        .content(mapper.writeValueAsString(userDtoToStartExc2))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("violations", notNullValue()));
    }

    @Test
    void updateUser() throws Exception {
        UserDto userDtoToStart = UserDto.builder().name("johny2").email("New345@mail.ru").build();

        UserDto userDto = UserDto.builder().id(1L).name("johny2").email("New345@mail.ru").build();

        when(userService.update(any(), anyLong()))
                .thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDtoToStart))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        UserDto userDtoToStartExc = UserDto.builder().email("345mail.ru")
                .build();

//        mvc.perform(post("/users")
//                        .content(mapper.writeValueAsString(userDtoToStartExc))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("violations", notNullValue()));
    }

    @Test
    void deleteUser() throws Exception {
        doNothing().when(userService).delete(anyLong());

        mvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUsers() throws Exception {

        when(userService.getAll(anyInt(), anyInt()))
                .thenReturn(List.of(
                        UserDto.builder().id(1L).name("johny").email("345@mail.ru").build(),
                        UserDto.builder().id(2L).name("Harry").email("9and3/4@mail.ru").build(),
                        UserDto.builder().id(3L).name("Scott").email("WorldFighter@mail.ru").build()
                ));

//        mvc.perform(get("/users")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void getUserById() throws Exception {
        UserDto userDto = UserDto.builder().id(1L).name("johny2").email("New345@mail.ru").build();

        when(userService.getOne(anyLong()))
                .thenReturn(userDto);

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }
}
