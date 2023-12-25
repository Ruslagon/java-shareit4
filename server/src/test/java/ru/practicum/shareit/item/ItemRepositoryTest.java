package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository repository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyBootstrappingByPersisting() {
        User user = User.builder().email("11234@mail.ru").name("tryHard").build();

        Assertions.assertNull(user.getId());
        em.persist(user);
        Assertions.assertNotNull(user.getId());

        Item item = Item.builder().available(true).owner(user).description("пустой предмет")
                .name("0").build();

        Assertions.assertNull(item.getId());
        em.persist(item);
        Assertions.assertNotNull(item.getId());
    }

    @Test
    void verifyRepositoryByPersisting() {
        User user = User.builder().email("1234@mail.ru").name("tryHard").build();

        Assertions.assertNull(user.getId());
        em.persist(user);
        Assertions.assertNotNull(user.getId());

        Item item = Item.builder().available(true).owner(user).description("пустой предмет")
                .name("0").build();

        Assertions.assertNull(item.getId());
        repository.save(item);
        Assertions.assertNotNull(item.getId());
    }

    @Test
    void findByIdAndWithUser() {
        User user = User.builder().email("1234@mail.ru").name("tryHard").build();

        Assertions.assertNull(user.getId());
        em.persist(user);
        Assertions.assertNotNull(user.getId());

        User otherUser = User.builder().email("12342@mail.ru").name("2tryHard").build();

        Assertions.assertNull(otherUser.getId());
        em.persist(otherUser);
        Assertions.assertNotNull(otherUser.getId());

        Item item = Item.builder().available(true).owner(user).description("пустой предмет")
                .name("0").build();

        Assertions.assertNull(item.getId());
        em.persist(item);
        Assertions.assertNotNull(item.getId());

        var returnedItemOptional = repository.findByIdAndWithUser(item.getId(), user.getId());

        assertThat(returnedItemOptional.isPresent(), equalTo(true));

        var returnedItem = returnedItemOptional.get();

        assertThat(returnedItem.getId(), equalTo(item.getId()));
        assertThat(returnedItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(returnedItem.getOwner(), equalTo(item.getOwner()));
        assertThat(returnedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(returnedItem.getName(), equalTo(item.getName()));

        returnedItemOptional = repository.findByIdAndWithUser(item.getId(), otherUser.getId());

        assertThat(returnedItemOptional.isPresent(), equalTo(false));

        em.clear();
    }

    @Test
    void findAllByOwnerId() {
        User user = User.builder().email("1234@mail.ru").name("tryHard").build();

        Assertions.assertNull(user.getId());
        em.persist(user);
        Assertions.assertNotNull(user.getId());

        Item item = Item.builder().available(true).owner(user).description("пустой предмет")
                .name("0").build();

        Assertions.assertNull(item.getId());
        em.persist(item);
        Assertions.assertNotNull(item.getId());

        Item item1 = Item.builder().owner(user).description("empty")
                .name("deep").available(true).build();
        em.persist(item1);
        Item item2 = Item.builder().owner(user).description("empty2")
                .name("2deep").available(false).build();
        em.persist(item2);
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Item> items = repository.findAllByOwnerId(user.getId(), pageRequest).getContent();

        assertThat(items, hasSize(3));
        for (Item item3 : items) {
            assertThat(item3.getOwner(), equalTo(user));
        }
        em.clear();
    }

    @Test
    void findAllByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingAndAvailableTrue() {
        User user = User.builder().email("1234@mail.ru").name("tryHard").build();

        Assertions.assertNull(user.getId());
        em.persist(user);
        Assertions.assertNotNull(user.getId());

        Item item = Item.builder().available(true).owner(user).description("пустой предмет")
                .name("empty2 on").build();

        Assertions.assertNull(item.getId());
        em.persist(item);
        Assertions.assertNotNull(item.getId());

        Item item1 = Item.builder().owner(user).description("empty")
                .name("deep").available(true).build();
        em.persist(item1);
        Item item2 = Item.builder().owner(user).description("empty2")
                .name("2deep").available(true).build();
        em.persist(item2);

        Item item3 = Item.builder().owner(user).description("empty2")
                .name("2deep").available(false).build();
        em.persist(item3);
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Item> items = repository
                .findAllByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingAndAvailableTrue("empty","empty", pageRequest)
                .getContent();

        assertThat(items, hasSize(3));

        items = repository
                .findAllByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingAndAvailableTrue("empty2","empty2", pageRequest)
                .getContent();

        assertThat(items, hasSize(2));


        items = repository
                .findAllByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingAndAvailableTrue("empty2 on","empty2 on", pageRequest)
                .getContent();

        assertThat(items, hasSize(1));
        em.clear();
    }

    @Test
    void findByIdAndHaveBookingsByUserId() {
        User user = User.builder().email("1234@mail.ru").name("tryHard").build();

        Assertions.assertNull(user.getId());
        em.persist(user);
        Assertions.assertNotNull(user.getId());

        User otherUser = User.builder().email("12342@mail.ru").name("2tryHard").build();

        Assertions.assertNull(otherUser.getId());
        em.persist(otherUser);
        Assertions.assertNotNull(otherUser.getId());

        Item item = Item.builder().available(true).owner(user).description("пустой предмет")
                .name("0").build();

        Assertions.assertNull(item.getId());
        em.persist(item);
        Assertions.assertNotNull(item.getId());

        Booking booking = Booking.builder().status(BookingStatus.APPROVED)
                .item(item).booker(otherUser).start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(5)).build();
        em.persist(booking);

        item.setBookings(List.of(booking));
        em.persist(item);

        var returnedItemOptional = repository.findByIdAndHaveBookingsByUserId(item.getId(), otherUser.getId(), BookingStatus.APPROVED, LocalDateTime.now().plusDays(10));

        assertThat(returnedItemOptional.isPresent(), equalTo(true));

        var returnedItem = returnedItemOptional.get();

        assertThat(returnedItem.getId(), equalTo(item.getId()));
        assertThat(returnedItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(returnedItem.getOwner(), equalTo(item.getOwner()));
        assertThat(returnedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(returnedItem.getName(), equalTo(item.getName()));
        assertThat(returnedItem.getBookings().get(0), equalTo(booking));

        returnedItemOptional = repository.findByIdAndHaveBookingsByUserId(item.getId(), user.getId(), BookingStatus.APPROVED, LocalDateTime.now().plusDays(100));
        assertThat(returnedItemOptional.isPresent(), equalTo(false));

        returnedItemOptional = repository.findByIdAndHaveBookingsByUserId(item.getId(), otherUser.getId(), BookingStatus.REJECTED, LocalDateTime.now().plusDays(100));
        assertThat(returnedItemOptional.isPresent(), equalTo(false));

        returnedItemOptional = repository.findByIdAndHaveBookingsByUserId(item.getId(), otherUser.getId(), BookingStatus.APPROVED, LocalDateTime.now());
        assertThat(returnedItemOptional.isPresent(), equalTo(false));

        em.clear();
    }

    @Test
    void findByOwnerIdAndId() {
        em.clear();
        User user = User.builder().email("1234sdf@mail.ru").name("tryHard").build();


        Assertions.assertNull(user.getId());
        em.persist(user);
        Assertions.assertNotNull(user.getId());

        User otherUser = User.builder().email("12342@mail.ru").name("2tryHard").build();

        Assertions.assertNull(otherUser.getId());
        em.persist(otherUser);
        Assertions.assertNotNull(otherUser.getId());

        Item item = Item.builder().available(true).owner(user).description("пустой предмет")
                .name("0").build();

        Assertions.assertNull(item.getId());
        em.persist(item);
        Assertions.assertNotNull(item.getId());

        Booking booking = Booking.builder().status(BookingStatus.APPROVED)
                .item(item).booker(otherUser).start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(5)).build();
        em.persist(booking);

        item.setBookings(List.of(booking));
        em.persist(item);

        Comment comment = Comment.builder().author(otherUser).text("сойдет")
                .created(LocalDateTime.now()).item(item).build();
        em.persist(comment);

        item.setComments(List.of(comment));
        em.persist(item);

        var returnedItemOptional = repository.findByOwnerIdAndId(user.getId(), item.getId());
        assertThat(returnedItemOptional.isPresent(), equalTo(true));

        var returnedItem = returnedItemOptional.get();
        assertThat(returnedItem.getId(), equalTo(item.getId()));
        assertThat(returnedItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(returnedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(returnedItem.getName(), equalTo(item.getName()));
        assertThat(returnedItem.getComments().get(0).getId(), equalTo(comment.getId()));
        assertThat(returnedItem.getRequest(), nullValue());

        returnedItemOptional = repository.findByOwnerIdAndId(otherUser.getId(), item.getId());
        assertThat(returnedItemOptional.isPresent(), equalTo(false));

        em.clear();
    }

    @Test
    void findByIdWithComments() {
        User user = User.builder().email("1234@mail.ru").name("tryHard").build();

        Assertions.assertNull(user.getId());
        em.persist(user);
        Assertions.assertNotNull(user.getId());

        User otherUser = User.builder().email("12342@mail.ru").name("2tryHard").build();

        Assertions.assertNull(otherUser.getId());
        em.persist(otherUser);
        Assertions.assertNotNull(otherUser.getId());

        Item item = Item.builder().available(true).owner(user).description("пустой предмет")
                .name("0").build();

        Assertions.assertNull(item.getId());
        em.persist(item);
        Assertions.assertNotNull(item.getId());

        Booking booking = Booking.builder().status(BookingStatus.APPROVED)
                .item(item).booker(otherUser).start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(5)).build();
        em.persist(booking);

        item.setBookings(List.of(booking));
        em.persist(item);

        Comment comment = Comment.builder().author(otherUser).text("сойдет")
                .created(LocalDateTime.now()).item(item).build();
        em.persist(comment);

        Comment comment2 = Comment.builder().author(otherUser).text("хотя нет")
                .created(LocalDateTime.now()).item(item).build();
        em.persist(comment);

        item.setComments(List.of(comment, comment2));
        em.persist(item);

        var returnedItemOptional = repository.findByIdWithComments(item.getId());
        assertThat(returnedItemOptional.isPresent(), equalTo(true));

        var returnedItem = returnedItemOptional.get();
        assertThat(returnedItem.getId(), equalTo(item.getId()));
        assertThat(returnedItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(returnedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(returnedItem.getName(), equalTo(item.getName()));
        assertThat(returnedItem.getComments().size(), equalTo(2));
        assertThat(returnedItem.getComments().get(0).getId(), equalTo(comment.getId()));
        assertThat(returnedItem.getComments().get(1).getId(), equalTo(comment2.getId()));
        assertThat(returnedItem.getRequest(), nullValue());

        returnedItemOptional = repository.findByIdWithComments(item.getId() + 1);
        assertThat(returnedItemOptional.isPresent(), equalTo(false));

        em.clear();
    }
}
