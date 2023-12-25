package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.DtoUserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    private final EntityManager em;
    private final UserService service;

    @Test
    void saveUser() {
        // given
        UserDto userDto = UserDto.builder().name("Harry").email("some@email.com").build();

        // when
        service.add(userDto);

        // then
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));

        em.clear();
    }

    @Test
    void getAllUsers() {
        // given
        List<UserDto> sourceUsers = List.of(
                UserDto.builder().email("ivan@email").name("Ivan").build(),
                UserDto.builder().email("petr@email").name("Petr").build(),
                UserDto.builder().email("vasilii@email").name("Vasilii").build()
        );

        for (UserDto user : sourceUsers) {
            User entity = DtoUserMapper.dtoToUser(user);
            em.persist(entity);
        }
        em.flush();

        // when
        List<UserDto> targetUsers = service.getAll(0, 50);

        // then
        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (UserDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    void getOne() {
        // given

        User newUser = User.builder().email("newMail@mail.ru").name("newGuy").build();
        em.persist(newUser);
        // when
        var foundedUser = service.getOne(newUser.getId());

        // then;

        assertThat(foundedUser.getId(), equalTo(newUser.getId()));
        assertThat(foundedUser.getName(), equalTo(newUser.getName()));
        assertThat(foundedUser.getEmail(), equalTo(newUser.getEmail()));

        em.clear();
    }

    @Test
    void delete() {
        User newUser = User.builder().email("newMail@mail.ru").name("newGuy").build();
        em.persist(newUser);

        service.delete(newUser.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        List<User> user = query.setParameter("email", newUser.getEmail())
                .getResultList();

        assertThat(user.size(), equalTo(0));
    }
}
