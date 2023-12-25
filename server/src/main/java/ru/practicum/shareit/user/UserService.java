package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.DtoUserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserDto add(UserDto userToAdd) {
        userToAdd.setId(null);
        return DtoUserMapper.userToDto(userRepository.save(DtoUserMapper.dtoToUser(userToAdd)));
    }

    @Transactional
    public UserDto update(UserDto userToUpdate, Long userId) {
        userToUpdate.setId(userId);
        var oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user по id - " + userId + " не найден"));
        DtoUserMapper.updateUserFromDto(userToUpdate, oldUser);
        return DtoUserMapper.userToDto(userRepository.save(oldUser));
    }

    public List<UserDto> getAll(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        return userRepository.findAll(pageRequest).map(DtoUserMapper::userToDto)
                .getContent();
    }

    public UserDto getOne(Long userId) {
        return DtoUserMapper.userToDto(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user по id - " + userId + " не найден")));
    }

    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

}
