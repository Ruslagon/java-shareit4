package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentTest;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemInfo;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.RequestId;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ItemServiceUnitTest {

    private ItemRepository itemRepository = Mockito.mock(ItemRepository.class);

    private UserRepository userRepository = Mockito.mock(UserRepository.class);

    private BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);

    private CommentRepository commentRepository = Mockito.mock(CommentRepository.class);

    private RequestRepository requestRepository = Mockito.mock(RequestRepository.class);

    private ItemService itemService = new ItemService(userRepository, itemRepository,
            bookingRepository, commentRepository, requestRepository);

    @Test
    void getOne() {
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(User.builder().build()));

        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        RequestId requestId = factory.createProjection(RequestId.class);
        requestId.setId(1L);

        LocalDateTime date = LocalDateTime.of(2023,10,10, 11, 11,11);
        //CommentDto commentDto = CommentDto.builder().text("я бы лучше сделал").build();
        CommentTest commentTest = factory.createProjection(CommentTest.class);
        commentTest.setId(1L);
        commentTest.setText("я бы лучше сделал");
        commentTest.setCreated(date);
        commentTest.setAuthor(User.builder().name("Alan Wake").build());

        ItemInfo itemInfo = factory.createProjection(ItemInfo.class);
        itemInfo.setId(1L);
        itemInfo.setAvailable(true);
        itemInfo.setDescription("лучший тест");
        itemInfo.setName("itemTest");
        itemInfo.setRequest(requestId);
        itemInfo.setComments(List.of(commentTest));

        Mockito.when(itemRepository.findByOwnerIdAndId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(itemInfo));

        Booking lastBooking = Booking.builder().id(1L).start(LocalDateTime.now().minusDays(4))
                .end(LocalDateTime.now().minusDays(3)).item(Item.builder().id(1L).build()).booker(User.builder().id(1L).build())
                .status(BookingStatus.APPROVED).build();
        Booking nextBooking = Booking.builder().id(2L).start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4)).item(Item.builder().id(1L).build()).booker(User.builder().id(1L).build())
                .status(BookingStatus.APPROVED).build();

        Mockito.when(bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(lastBooking);
        Mockito.when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(nextBooking);

        var itemOutput = itemService.getOne(1L, 1L);

        assertThat(itemOutput.getDescription(), equalTo(itemInfo.getDescription()));
        assertThat(itemOutput.getId(), equalTo(itemInfo.getId()));
        assertThat(itemOutput.getAvailable(), equalTo(itemInfo.getAvailable()));
        assertThat(itemOutput.getName(), equalTo(itemInfo.getName()));
        assertThat(itemOutput.getRequestId(), equalTo(itemInfo.getRequest().getId()));
        assertThat(itemOutput.getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(itemOutput.getLastBooking().getStatus(), equalTo(lastBooking.getStatus()));
        assertThat(itemOutput.getLastBooking().getBookerId(), equalTo(lastBooking.getBooker().getId()));
        assertThat(itemOutput.getLastBooking().getStart(), equalTo(lastBooking.getStart()));
        assertThat(itemOutput.getLastBooking().getEnd(), equalTo(lastBooking.getEnd()));

        assertThat(itemOutput.getNextBooking().getId(), equalTo(nextBooking.getId()));
        assertThat(itemOutput.getNextBooking().getStatus(), equalTo(nextBooking.getStatus()));
        assertThat(itemOutput.getNextBooking().getBookerId(), equalTo(nextBooking.getBooker().getId()));
        assertThat(itemOutput.getNextBooking().getStart(), equalTo(nextBooking.getStart()));
        assertThat(itemOutput.getNextBooking().getEnd(), equalTo(nextBooking.getEnd()));
        assertThat(itemOutput.getComments().get(0).getId(), equalTo(commentTest.getId()));
    }

    @Test
    void addComment() {
        CommentDto commentDto = CommentDto.builder().text("я бы лучше сделал").build();
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(User.builder().name("Tod").email("microsoftBuySkyrim@mail.ru").id(1L).build()));

        Mockito.when(itemRepository.findByIdAndHaveBookingsByUserId(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(Item.builder().build()));

        Mockito.when(commentRepository.save(Mockito.any()))
                .thenReturn(Comment.builder().id(1L).build());

        LocalDateTime date = LocalDateTime.of(2023,10,10, 11, 11,11);
        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        CommentTest commentTest = factory.createProjection(CommentTest.class);
        commentTest.setId(1L);
        commentTest.setText(commentTest.getText());
        commentTest.setCreated(date);
        commentTest.setAuthor(User.builder().name("Tod").build());
        Mockito.when(commentRepository.findByIdAndAuthorId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(commentTest);

        var commentOutput = itemService.addComment(1L,1L, commentDto);

        assertThat(commentOutput.getId(), equalTo(commentTest.getId()));
        assertThat(commentOutput.getAuthorName(), equalTo(commentTest.getAuthorName()));
        assertThat(commentOutput.getCreated(), equalTo(commentTest.getCreated()));
        assertThat(commentOutput.getText(), equalTo(commentTest.getText()));
    }
}
