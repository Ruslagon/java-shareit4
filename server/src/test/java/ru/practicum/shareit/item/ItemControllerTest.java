package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    ProjectionFactory factory = new SpelAwareProxyProjectionFactory();

    @Test
    void saveNewRequest() throws Exception {
        User user = User.builder().id(1L).name("User12q").email("araara@gmail.jp").build();

        ItemDto starterItem = ItemDto.builder().name("itemaaaaaaaa").description("AAAAAAAA")
                .available(true).build();

        ItemDto outputItem = ItemDto.builder().name("itemaaaaaaaa").description("AAAAAAAA")
                .available(true).id(1L).owner(user).build();

        when(itemService.add(anyLong(), any()))
                .thenReturn(outputItem);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(starterItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outputItem.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(starterItem.getDescription())))
                .andExpect(jsonPath("$.name", is(starterItem.getName())))
                .andExpect(jsonPath("$.available", is(starterItem.getAvailable())))
                .andExpect(jsonPath("$.owner.id", is(user.getId().intValue())));

        ItemDto starterItemExc1 = ItemDto.builder().description("AAAAAAAA")
                .available(true).build();

//        mvc.perform(post("/items")
//                        .header("X-Sharer-User-Id", "1")
//                        .content(mapper.writeValueAsString(starterItemExc1))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.violations", notNullValue()));

        ItemDto starterItemExc2 = ItemDto.builder().name("itemaaaaaaaa")
                .available(true).build();

//        mvc.perform(post("/items")
//                        .header("X-Sharer-User-Id", "1")
//                        .content(mapper.writeValueAsString(starterItemExc2))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.violations", notNullValue()));

        ItemDto starterItemExc3 = ItemDto.builder().name("itemaaaaaaaa").description("AAAAAAAA")
                .build();

//        mvc.perform(post("/items")
//                        .header("X-Sharer-User-Id", "1")
//                        .content(mapper.writeValueAsString(starterItemExc3))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.violations", notNullValue()));
    }

    @Test
    void updateItem() throws Exception {
        User user = User.builder().id(1L).name("User12q").email("araara@gmail.jp").build();

        ItemDto starterItem = ItemDto.builder().name("newitemaaaaaaaa").description("newAAAAAAAA")
                .available(false).build();

        ItemDto outputItem = ItemDto.builder().name("newitemaaaaaaaa").description("newAAAAAAAA")
                .available(false).id(1L).owner(user).build();

        when(itemService.update(anyLong(), any(), anyLong()))
                .thenReturn(outputItem);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(starterItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outputItem.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(starterItem.getDescription())))
                .andExpect(jsonPath("$.name", is(starterItem.getName())))
                .andExpect(jsonPath("$.available", is(starterItem.getAvailable())))
                .andExpect(jsonPath("$.owner.id", is(user.getId().intValue())));
    }

    @Test
    void getAllItemsForUser() throws Exception {
        User user = User.builder().id(1L).name("User12q").email("araara@gmail.jp").build();

        when(itemService.getAllForUser(anyLong(),anyInt(), anyInt()))
                .thenReturn(List.of(
                        ItemDto.builder().name("newitemaaaaaaaa1").description("newAAAAAAAA").available(false).id(1L).owner(user).build(),
                        ItemDto.builder().name("newitemaaaaaaaa2").description("newAAAAAAAA").available(true).id(2L).owner(user).build(),
                        ItemDto.builder().name("newitemaaaaaaaa3").description("newAAAAAAAA").available(false).id(3L).owner(user).build()
                        ));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void search() throws Exception {
        User user = User.builder().id(1L).name("User12q").email("araara@gmail.jp").build();

        when(itemService.search(anyLong(), anyString(),anyInt(), anyInt()))
                .thenReturn(List.of(
                        ItemDto.builder().name("Harry pot of the world").description("newAAAAAAAA").available(false).id(1L).owner(user).build(),
                        ItemDto.builder().name("Harry Potter and dark arts").description("newAAAAAAAA").available(true).id(2L).owner(user).build(),
                        ItemDto.builder().name("Katia Grotter").description("yes, its joke about Harry Potter").available(false).id(3L).owner(user).build()
                ));

        mvc.perform(get("/items/search")
                        .param("text", "harry pot")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void deleteItem() throws Exception {
        doNothing().when(itemService).delete(anyLong(), anyLong());

        mvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addComment() throws Exception {
        LocalDateTime date = LocalDateTime.of(2023,10,10, 11, 11,11);

        CommentDto commentDto = CommentDto.builder().text("я бы лучше сделал").build();

        CommentTest commentTest = factory.createProjection(CommentTest.class);
        commentTest.setId(1L);
        commentTest.setText("я бы лучше сделал");
        commentTest.setCreated(date);
        commentTest.setAuthor(User.builder().name("Alan Wake").build());


        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentTest);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentTest.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentTest.getText())))
                .andExpect(jsonPath("$.authorName", is(commentTest.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentTest.getCreated().toString())));

        CommentDto commentDtoExp = CommentDto.builder().build();

//        mvc.perform(post("/items/1/comment")
//                        .header("X-Sharer-User-Id", "1")
//                        .content(mapper.writeValueAsString(commentDtoExp))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.violations", notNullValue()));
    }

    @Test
    void getRequestById() throws Exception {
        User user = User.builder().id(1L).name("User12q").email("araara@gmail.jp").build();

        ItemDto starterItem = ItemDto.builder().name("newitemaaaaaaaa").description("newAAAAAAAA")
                .available(false).build();
        BookingDto lastBookingDto = BookingDto.builder().id(1L).start(LocalDateTime.now().minusDays(4))
                .end(LocalDateTime.now().minusDays(3)).itemId(1L).bookerId(1L).status(BookingStatus.APPROVED).build();

        BookingDto nextBookingDto = BookingDto.builder().id(2L).start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4)).itemId(1L).bookerId(1L).status(BookingStatus.APPROVED).build();

        ItemInfoDto outputItem = ItemInfoDto.builder().name("newitemaaaaaaaa").description("newAAAAAAAA")
                .available(false).id(1L).owner(user).requestId(1L).nextBooking(nextBookingDto).lastBooking(lastBookingDto)
                .build();

        when(itemService.getOne(anyLong(),anyLong()))
                .thenReturn(outputItem);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outputItem.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(starterItem.getDescription())))
                .andExpect(jsonPath("$.name", is(starterItem.getName())))
                .andExpect(jsonPath("$.available", is(starterItem.getAvailable())))
                .andExpect(jsonPath("$.nextBooking.id",is(nextBookingDto.getId().intValue())))
                .andExpect(jsonPath("$.nextBooking.id",is(nextBookingDto.getId().intValue())))
                .andExpect(jsonPath("$.owner.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.requestId", is(outputItem.getRequestId().intValue())));
    }
}
