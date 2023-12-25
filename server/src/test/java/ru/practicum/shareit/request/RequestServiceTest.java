package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
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
public class RequestServiceTest {

    private final EntityManager em;
    private final RequestService service;

    @Test
    void saveRequest() {
        // given
        User user = User.builder().name("Harry").email("some@email.com").build();
        em.persist(user);
        RequestDto requestDto = RequestDto.builder().description("хочу спать").build();

        // when
        RequestDto requestDtoAfter = service.add(user.getId(), requestDto);

        // then
        TypedQuery<Request> query = em.createQuery("Select r from Request r where r.description = :description", Request.class);
        Request request = query.setParameter("description", requestDto.getDescription())
                .getSingleResult();

        assertThat(request.getId(), notNullValue());
        assertThat(request.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(request.getCreated(), equalTo(requestDtoAfter.getCreated()));
    }

    @Test
    void getRequestsForUsers() {
        User user = User.builder().name("Harry").email("someone@email.com").build();
        em.persist(user);
        LocalDateTime dateTime = LocalDateTime.of(2020, 12,12,12,12,21,12);

        List<Request> sourceRequests = List.of(
                Request.builder().description("хочу спать").created(dateTime).user(user).build(),
                Request.builder().description("хочу есть").created(dateTime.plusDays(10)).user(user).build(),
                Request.builder().description("хочу отдохнуть").created(dateTime.plusDays(15)).user(user).build()
        );

        for (Request request : sourceRequests) {
            em.persist(request);
        }

        List<RequestDto> targetRequests = service.getAllForUser(user.getId(), 0,10);

        // then
        assertThat(targetRequests, hasSize(sourceRequests.size()));
        for (Request sourceRequest : sourceRequests) {
            assertThat(targetRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceRequest.getDescription())),
                    hasProperty("created", equalTo(sourceRequest.getCreated()))
            )));
        }
    }

    @Test
    void getAllOthersRequests() {
        User user = User.builder().name("Harry").email("someone@email.com").build();
        User user2 = User.builder().name("Harry2").email("someone2@email.com").build();
        em.persist(user);
        em.persist(user2);
        LocalDateTime dateTime = LocalDateTime.of(2020, 12,12,12,12,21,12);

        List<Request> sourceRequests = List.of(
                Request.builder().description("хочу спать").created(dateTime).user(user).build(),
                Request.builder().description("хочу есть").created(dateTime.plusDays(10)).user(user).build(),
                Request.builder().description("хочу отдохнуть").created(dateTime.plusDays(15)).user(user).build()
        );

        for (Request request : sourceRequests) {
            em.persist(request);
        }

        List<RequestDto> targetRequests1 = service.getAll(user.getId(), 0, 10);

        assertThat(targetRequests1, hasSize(0));

        List<RequestDto> targetRequests2 = service.getAll(user2.getId(), 0,10);

        // then
        assertThat(targetRequests2, hasSize(sourceRequests.size()));
        for (Request sourceRequest : sourceRequests) {
            assertThat(targetRequests2, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceRequest.getDescription())),
                    hasProperty("created", equalTo(sourceRequest.getCreated()))
            )));
        }
    }
}
