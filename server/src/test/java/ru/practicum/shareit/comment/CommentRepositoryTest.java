package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepository repository;


    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyBootstrappingByPersisting() {
        User user = User.builder().email("1234@mail.ru").name("tryHard").build();
        User user2 = User.builder().email("12345@mail.ru").name("tryHard2").build();
        em.persist(user);
        em.persist(user2);
        Item item = Item.builder().owner(user).available(true).name("item1").description("item for test").build();
        em.persist(item);
        Booking booking = Booking.builder().booker(user2).status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(4)).item(item).build();
        em.persist(booking);

        Comment comment = Comment.builder().text("good staff").created(LocalDateTime.now().plusDays(10))
                .author(user2).item(item).build();

        Assertions.assertNull(comment.getId());
        em.persist(comment);
        Assertions.assertNotNull(comment.getId());
    }

    @Test
    void verifyRepositoryByPersisting() {
        User user = User.builder().email("11234@mail.ru").name("TryHard").build();
        User user2 = User.builder().email("112345@mail.ru").name("TryHard2").build();
        em.persist(user);
        em.persist(user2);
        Item item = Item.builder().owner(user).available(true).name("item1").description("item for test").build();
        em.persist(item);
        Booking booking = Booking.builder().booker(user2).status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(4)).item(item).build();
        em.persist(booking);

        Comment comment = Comment.builder().text("good staff").created(LocalDateTime.now().plusDays(10))
                .author(user2).item(item).build();

        Assertions.assertNull(comment.getId());
        repository.save(comment);
        Assertions.assertNotNull(comment.getId());
    }

    @Test
    void testFindByIdAndAuthorId() {
        User user = User.builder().email("1234@mail.ru").name("tryHard").build();
        User user2 = User.builder().email("12345@mail.ru").name("tryHard2").build();
        em.persist(user);
        em.persist(user2);
        Item item = Item.builder().owner(user).available(true).name("item1").description("item for test").build();
        em.persist(item);
        Booking booking = Booking.builder().booker(user2).status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(4)).item(item).build();
        em.persist(booking);

        Comment comment = Comment.builder().text("good staff").created(LocalDateTime.now().plusDays(10))
                .author(user2).item(item).build();

        Assertions.assertNull(comment.getId());
        em.persist(comment);
        Assertions.assertNotNull(comment.getId());


        var commentGot = repository.findByIdAndAuthorId(comment.getId(), user2.getId());
        assertEquals(commentGot.getId(), comment.getId());
        assertEquals(commentGot.getCreated(), comment.getCreated());
        assertEquals(commentGot.getAuthorName(), user2.getName());
        assertEquals(commentGot.getText(), comment.getText());

        commentGot = repository.findByIdAndAuthorId(comment.getId(), user.getId());
        Assertions.assertNull(commentGot);
    }
}
