package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    private final EntityManager em;

    private final ItemService service;

    @Test
    void saveItem() {
        // given
        User user = User.builder().name("Harry").email("some@email.com").build();
        em.persist(user);
        ItemDto itemDto = ItemDto.builder().owner(user).name("new item").description("this IS a new item")
                .available(true).build();
        // when
        ItemDto itemDtoAfter = service.add(user.getId(), itemDto);

        // then
        TypedQuery<Item> query = em.createQuery("Select it from Item it where it.description = :description", Item.class);
        Item item = query.setParameter("description", itemDto.getDescription())
                .getSingleResult();

        assertThat(item.getId(), equalTo(itemDtoAfter.getId()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getOwner(), equalTo(user));
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void updateItem() {
        // given
        User user = User.builder().name("Harry3").email("some3@email.com").build();
        em.persist(user);
        Item item = Item.builder().owner(user).name("new item3").description("this IS a new item3")
                .available(true).build();
        em.persist(item);
        ItemDto itemDto = ItemDto.builder().name("new item3 or more?").description("this IS a new item3 MoRE")
                .available(false).build();

        // when
        ItemDto itemDtoAfter = service.update(user.getId(), itemDto, item.getId());

        // then
        TypedQuery<Item> query = em.createQuery("Select it from Item it where it.description = :description", Item.class);
        Item itemResult = query.setParameter("description", itemDto.getDescription())
                .getSingleResult();

        assertThat(itemResult.getId(), equalTo(itemDtoAfter.getId()));
        assertThat(itemResult.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(itemResult.getOwner(), equalTo(user));
        assertThat(itemResult.getName(), equalTo(itemDto.getName()));
        assertThat(itemResult.getAvailable(), equalTo(itemDto.getAvailable()));

        List<Item> itemResultNull = query.setParameter("description", "this IS a new item3")
                .getResultStream().collect(Collectors.toList());

        assertThat(itemResultNull.size(), equalTo(0));

    }

    @Test
    void getAllForUser() {
        User user = User.builder().name("Harry2").email("2someone@email.com").build();
        em.persist(user);

        List<Item> sourceItems = List.of(
                Item.builder().owner(user).name("new item").description("this IS a new item")
                        .available(true).build(),
                Item.builder().owner(user).name("new item2").description("this IS a new item2")
                        .available(true).build(),
                Item.builder().owner(user).name("new item3").description("this IS a new item3")
                        .available(true).build()
        );

        for (Item item : sourceItems) {
            em.persist(item);
        }

        List<ItemDto> targetItems = service.getAllForUser(user.getId(), 0,10);

        // then
        assertThat(targetItems, hasSize(sourceItems.size()));
        for (Item sourceItem : sourceItems) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", equalTo(sourceItem.getId())),
                    hasProperty("description", equalTo(sourceItem.getDescription())),
                    hasProperty("name", equalTo(sourceItem.getName())),
                    hasProperty("available", equalTo(sourceItem.getAvailable()))
            )));
        }
    }

    @Test
    void search() {
        User user = User.builder().name("Harry4").email("4someone@email.com").build();
        em.persist(user);
        User user2 = User.builder().name("Harry5").email("5someone@email.com").build();
        em.persist(user2);

        List<Item> sourceItems = List.of(
                Item.builder().owner(user).name("IteMKimono").description("1this IS a new item")
                        .available(true).build(),
                Item.builder().owner(user).name("IteMKing").description("2this IS a new item2")
                        .available(true).build(),
                Item.builder().owner(user).name("IteMKino").description("3this IS a new item3")
                        .available(true).build()
        );

        List<Item> sourceItems2 = List.of(
                Item.builder().owner(user).name("IteMKinder").description("1this IS a new item")
                        .available(true).build(),
                Item.builder().owner(user).name("ItemKit").description("2this IS a new item2")
                        .available(true).build(),
                Item.builder().owner(user).name("IteMKi").description("3this IS a new item3")
                        .available(true).build()
        );

        for (Item item : sourceItems) {
            em.persist(item);
        }
        for (Item item : sourceItems2) {
            em.persist(item);
        }

        List<ItemDto> targetItems = service.search(user2.getId(),"Itemki", 0,10);

        // then
        assertThat(targetItems, hasSize(6));

        targetItems = service.search(user.getId(),"Itemkin", 0,10);
        assertThat(targetItems, hasSize(3));

        targetItems = service.search(user.getId(),"Itemkim", 0,10);
        assertThat(targetItems, hasSize(1));

        targetItems = service.search(user.getId(),"Itemkit", 0,10);
        assertThat(targetItems, hasSize(1));
    }

    @Test
    void delete() {
        // given
        User user = User.builder().name("Harry7").email("some37@email.com").build();
        em.persist(user);
        Item item = Item.builder().owner(user).name("new item7").description("this IS a new item37")
                .available(true).build();
        em.persist(item);

        TypedQuery<Item> query = em.createQuery("Select it from Item it where it.description = :description", Item.class);
        Item itemResult = query.setParameter("description", item.getDescription())
                .getSingleResult();

        assertThat(itemResult.getId(), equalTo(item.getId()));
        assertThat(itemResult.getDescription(), equalTo(item.getDescription()));
        assertThat(itemResult.getOwner(), equalTo(user));
        assertThat(itemResult.getName(), equalTo(item.getName()));
        assertThat(itemResult.getAvailable(), equalTo(item.getAvailable()));

        // when
        service.delete(user.getId(), item.getId());

        // then
        List<Item> itemResultNull = query.setParameter("description", "this IS a new item37")
                .getResultStream().collect(Collectors.toList());

        assertThat(itemResultNull.size(), equalTo(0));
    }
}
