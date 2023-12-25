package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.dto.DtoRequestMapper;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    @Transactional
    public RequestDto add(Long userId, RequestDto requestDto) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user по id - " + userId + " не найден"));

        return DtoRequestMapper.requestToDto(requestRepository.save(DtoRequestMapper.dtoToRequest(requestDto, user)));
    }

    List<RequestDto> getAllForUser(Long userId, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user по id - " + userId + " не найден"));

        return requestRepository.findAllByUserIdOrderByCreatedDesc(userId, pageRequest).map(DtoRequestMapper::requestToDtoWithItems)
                .getContent();
    }

    public RequestDto getOne(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user по id - " + userId + " не найден"));

        return DtoRequestMapper.requestToDtoWithItems(requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("запрос по id - " + requestId + " не найден")));
    }

    List<RequestDto> getAll(Long userId, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user по id - " + userId + " не найден"));

        return requestRepository.findAllByUserIdNotOrderByCreatedDesc(userId, pageRequest).map(DtoRequestMapper::requestToDtoWithItems)
                .getContent();
    }
}
