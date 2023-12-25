package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.model.ItemBookingsInfo;
import ru.practicum.shareit.user.model.UserInfoId;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    ProjectionFactory factory = new SpelAwareProxyProjectionFactory();

    @Test
    void saveNewBooking() throws Exception {
        LocalDateTime date = LocalDateTime.of(2024,10,10, 11, 11,11);

        BookingDto bookingDtoStarter = BookingDto.builder().start(date.plusDays(2)).end(date.plusDays(6)).itemId(1L).build();

        UserInfoId userInfoId = UserInfoId.builder().id(1L).build();
        ItemBookingsInfo itemBookingsInfo = ItemBookingsInfo.builder().id(1L).name("item!1").build();
        BookingInfoDto bookingDtoOutput = BookingInfoDto.builder().id(1L).itemId(1L).bookerId(1L)
                .start(date.plusDays(2)).end(date.plusDays(6)).status(BookingStatus.APPROVED)
                .booker(userInfoId).item(itemBookingsInfo).build();

        when(bookingService.add(anyLong(), any()))
                .thenReturn(bookingDtoOutput);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDtoStarter))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOutput.getStart().toString())))
                .andExpect(jsonPath("$.itemId", is(bookingDtoOutput.getItemId().intValue())))
                .andExpect(jsonPath("$.end", is(bookingDtoOutput.getEnd().toString())))
                .andExpect(jsonPath("$.bookerId", is(bookingDtoOutput.getBookerId().intValue())))
                .andExpect(jsonPath("$.status", is(bookingDtoOutput.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOutput.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOutput.getItem().getId().intValue())));

        var bookingDtoStarterExp = bookingDtoStarter;
        bookingDtoStarterExp.setStart(date.minusYears(10));

//        mvc.perform(post("/bookings")
//                        .header("X-Sharer-User-Id", "1")
//                        .content(mapper.writeValueAsString(bookingDtoStarterExp))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())jhjhj
//                .andExpect(jsonPath("$.violations", notNullValue()));

        var bookingDtoStarterExp2 = bookingDtoStarter;
        bookingDtoStarterExp.setItemId(null);

//        mvc.perform(post("/bookings")
//                        .header("X-Sharer-User-Id", "1")
//                        .content(mapper.writeValueAsString(bookingDtoStarterExp))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.violations", notNullValue()));
    }

    @Test
    void updateBooking() throws Exception {
        LocalDateTime date = LocalDateTime.of(2024,10,10, 11, 11,11);

        UserInfoId userInfoId = UserInfoId.builder().id(1L).build();
        ItemBookingsInfo itemBookingsInfo = ItemBookingsInfo.builder().id(1L).name("item!1").build();
        BookingInfoDto bookingDtoOutput = BookingInfoDto.builder().id(1L).itemId(1L).bookerId(1L)
                .start(date.plusDays(2)).end(date.plusDays(6)).status(BookingStatus.APPROVED)
                .booker(userInfoId).item(itemBookingsInfo).build();

        when(bookingService.update(anyLong(), anyLong(), any()))
                .thenReturn(bookingDtoOutput);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOutput.getStart().toString())))
                .andExpect(jsonPath("$.itemId", is(bookingDtoOutput.getItemId().intValue())))
                .andExpect(jsonPath("$.end", is(bookingDtoOutput.getEnd().toString())))
                .andExpect(jsonPath("$.bookerId", is(bookingDtoOutput.getBookerId().intValue())))
                .andExpect(jsonPath("$.status", is(bookingDtoOutput.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOutput.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOutput.getItem().getId().intValue())));
    }

    @Test
    void deleteBooking() throws Exception {
        doNothing().when(bookingService).delete(anyLong(), anyLong());

        mvc.perform(delete("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getUsersBookings() throws Exception {
        LocalDateTime date = LocalDateTime.of(2024,10,10, 11, 11,11);

        UserInfoId userInfoId = UserInfoId.builder().id(1L).build();
        ItemBookingsInfo itemBookingsInfo = ItemBookingsInfo.builder().id(1L).name("item!1").build();

        when(bookingService.getBookersBookings(anyLong(),any(), anyInt(), anyInt()))
                .thenReturn(List.of(
                        BookingInfoDto.builder().id(1L).itemId(1L).bookerId(1L)
                                .start(date.plusDays(2)).end(date.plusDays(6)).status(BookingStatus.APPROVED)
                                .booker(userInfoId).item(itemBookingsInfo).build(),
                        BookingInfoDto.builder().id(2L).itemId(1L).bookerId(1L)
                                .start(date.plusDays(10)).end(date.plusDays(16)).status(BookingStatus.REJECTED)
                                .booker(userInfoId).item(itemBookingsInfo).build()));

//        mvc.perform(get("/bookings")
//                        .header("X-Sharer-User-Id", "1")
//                        .param("from", "0")
//                        .param("size", "10")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())jjh
//                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getBooking() throws Exception {
        LocalDateTime date = LocalDateTime.of(2024,10,10, 11, 11,11);

        UserInfoId userInfoId = UserInfoId.builder().id(1L).build();
        ItemBookingsInfo itemBookingsInfo = ItemBookingsInfo.builder().id(1L).name("item!1").build();
        BookingInfoDto bookingDtoOutput = BookingInfoDto.builder().id(1L).itemId(1L).bookerId(1L)
                .start(date.plusDays(2)).end(date.plusDays(6)).status(BookingStatus.APPROVED)
                .booker(userInfoId).item(itemBookingsInfo).build();

        when(bookingService.getOne(anyLong(),anyLong()))
                .thenReturn(bookingDtoOutput);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOutput.getStart().toString())))
                .andExpect(jsonPath("$.itemId", is(bookingDtoOutput.getItemId().intValue())))
                .andExpect(jsonPath("$.end", is(bookingDtoOutput.getEnd().toString())))
                .andExpect(jsonPath("$.bookerId", is(bookingDtoOutput.getBookerId().intValue())))
                .andExpect(jsonPath("$.status", is(bookingDtoOutput.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOutput.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOutput.getItem().getId().intValue())));
    }

    @Test
    void getOwnersBookings() throws Exception {
        LocalDateTime date = LocalDateTime.of(2024,10,10, 11, 11,11);

        UserInfoId userInfoId = UserInfoId.builder().id(1L).build();
        ItemBookingsInfo itemBookingsInfo = ItemBookingsInfo.builder().id(1L).name("item!1").build();

        when(bookingService.getOwnersBookings(anyLong(),any(), anyInt(), anyInt()))
                .thenReturn(List.of(
                        BookingInfoDto.builder().id(1L).itemId(1L).bookerId(1L)
                                .start(date.plusDays(2)).end(date.plusDays(6)).status(BookingStatus.APPROVED)
                                .booker(userInfoId).item(itemBookingsInfo).build(),
                        BookingInfoDto.builder().id(2L).itemId(1L).bookerId(1L)
                                .start(date.plusDays(10)).end(date.plusDays(16)).status(BookingStatus.REJECTED)
                                .booker(userInfoId).item(itemBookingsInfo).build()));

//        mvc.perform(get("/bookings/owner")
//                        .header("X-Sharer-User-Id", "1")
//                        .param("from", "0")
//                        .param("size", "10")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())jhjh
//                .andExpect(jsonPath("$", hasSize(2)));
    }
}
