package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.data.projection.ProjectionFactory;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.dto.CommentTest;
import ru.practicum.shareit.item.dto.DtoItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemInfoRequest;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    ProjectionFactory factory = new SpelAwareProxyProjectionFactory();

    @Test
    void testRequestDto() throws Exception {
        User user = User.builder().id(1L).name("first").email("123@mail.ru").build();

        LocalDateTime date = LocalDateTime.of(2023,10,10, 11, 11,11);

        BookingDto lastBookingDto = BookingDto.builder().id(1L).start(LocalDateTime.now().minusDays(4))
                .end(LocalDateTime.now().minusDays(3)).itemId(1L).bookerId(1L).status(BookingStatus.APPROVED).build();

        BookingDto nextBookingDto = BookingDto.builder().id(2L).start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4)).itemId(1L).bookerId(1L).status(BookingStatus.APPROVED).build();

        CommentTest commentTest = factory.createProjection(CommentTest.class);
        commentTest.setId(1L);
        commentTest.setText("я бы лучше сделал");
        commentTest.setCreated(date);
        commentTest.setAuthor(User.builder().name("Alan Wake").build());

        CommentTest commentTest2 = factory.createProjection(CommentTest.class);
        commentTest2.setId(2L);
        commentTest2.setText("гениально, никто лучше бы не сделал");
        commentTest2.setCreated(date);
        commentTest2.setAuthor(User.builder().name("Mr.Scratch").build());


        List<CommentTest> comments = List.of(commentTest, commentTest2);

        ItemDto item = ItemDto.builder().name("newitemaaaaaaaa").description("newAAAAAAAA")
                .available(true).id(1L).owner(user).requestId(1L).nextBooking(nextBookingDto).lastBooking(lastBookingDto)
                .comments(comments).build();

        JsonContent<ItemDto> result = json.write(item);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("newitemaaaaaaaa");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("newAAAAAAAA");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(item.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.owner.id").isEqualTo(user.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.owner.name").isEqualTo(user.getName());
        assertThat(result).extractingJsonPathStringValue("$.owner.email").isEqualTo(user.getEmail());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(nextBookingDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.status").isEqualTo(nextBookingDto.getStatus().toString());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(lastBookingDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.status").isEqualTo(lastBookingDto.getStatus().toString());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].authorName").contains(commentTest.getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.comments.[1].authorName").contains(commentTest2.getAuthorName());
    }

    @Test
    void itemDtoMapper() {

        Item item = Item.builder().id(1L).request(Request.builder().id(2L).build()).description("item for mapTest")
                .name("test item").available(true).build();

        ItemInfoRequest itemOutput = DtoItemMapper.itemForRequest(item);

        assertThat(itemOutput.getId(), equalTo(item.getId()));
        assertThat(itemOutput.getRequestId(), equalTo(item.getRequest().getId()));
        assertThat(itemOutput.getDescription(), equalTo(item.getDescription()));
        assertThat(itemOutput.getName(), equalTo(item.getName()));
        assertThat(itemOutput.getAvailable(), equalTo(item.getAvailable()));
    }
}
