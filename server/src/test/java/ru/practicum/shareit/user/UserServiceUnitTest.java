package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;

public class UserServiceUnitTest {
    UserRepository userRepository = Mockito.mock(UserRepository.class);

    private UserService userService = new UserService(userRepository);

    @Test
    void update() {
        UserDto userDtoToUpdate = UserDto.builder().name("userNew").email("NewEmail@mail.ru").build();
        User newUser = User.builder().id(1L).name("userNew").email("NewEmail@mail.ru").build();
        User oldUser = User.builder().id(1L).name("oldUser").email("oldMail@mail.com").build();

        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(newUser);
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(oldUser));

        var newUserDto = userService.update(userDtoToUpdate, 1L);

        assertThat(newUserDto.getId(), equalTo(1L));
        assertThat(newUserDto.getName(), equalTo(userDtoToUpdate.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDtoToUpdate.getEmail()));
    }
}
