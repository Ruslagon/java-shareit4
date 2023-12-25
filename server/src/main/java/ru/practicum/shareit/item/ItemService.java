package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.DtoBookingMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentTest;
import ru.practicum.shareit.comment.dto.DtoCommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.model.BadRequest;
import ru.practicum.shareit.item.dto.DtoItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.ItemInfo;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private final RequestRepository requestRepository;

    @Transactional
    public ItemDto add(Long userId, ItemDto itemDto) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user по id - " + userId + " не найден"));
        Request request;
        if (itemDto.getRequestId() != null) {
            request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new EntityNotFoundException("request по id - " + itemDto.getRequestId() + " не найден"));
        } else {
            request = null;
        }
        itemDto.setOwner(user);
        return DtoItemMapper.itemToDto(itemRepository.save(DtoItemMapper.dtoToItem(itemDto, request)));
    }

    @Transactional
    public ItemDto update(Long userId, ItemDto itemDto, Long itemId) {
        itemDto.setId(itemId);
        var item = itemRepository.findByIdAndWithUser(itemId, userId)
                .orElseThrow(() -> new EntityNotFoundException("user - " + userId + " не владеет item - " + itemId));
        return DtoItemMapper.itemToDto(itemRepository.save(DtoItemMapper.updateItemFromDto(itemDto, item)));
    }

    public List<ItemDto> getAllForUser(Long userId, Integer from, Integer size) {
        LocalDateTime now = LocalDateTime.now();
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user по id - " + userId + " не найден"));
        return itemRepository.findAllByOwnerId(userId, pageRequest).stream().map(DtoItemMapper::itemToDtoWithoutUsers).peek(itemDto -> {
            itemDto.setLastBooking(DtoBookingMapper.bookingToDtoWithoutItems(bookingRepository
                    .findFirstByItemIdAndStartBeforeOrderByStartDesc(itemDto.getId(), now)));
            itemDto.setNextBooking(DtoBookingMapper.bookingToDtoWithoutItems(bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemDto.getId(), now, BookingStatus.APPROVED)));
                }).collect(Collectors.toList());
    }

    public ItemInfoDto getOne(Long userId, Long itemId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user по id - " + userId + " не найден"));
        Optional<ItemInfo> itemInfo = itemRepository.findByOwnerIdAndId(userId, itemId);
        if (itemInfo.isPresent()) {
            LocalDateTime now = LocalDateTime.now();
            var itemInfoDto = DtoItemMapper.itemInfoToDtoWithoutUsers(itemInfo.get());
            itemInfoDto.setLastBooking(DtoBookingMapper.bookingToDtoWithoutItems(bookingRepository
                    .findFirstByItemIdAndStartBeforeOrderByStartDesc(itemId, now)));
            itemInfoDto.setNextBooking(DtoBookingMapper.bookingToDtoWithoutItems(bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId, now, BookingStatus.APPROVED)));
            return itemInfoDto;
        }
        var dtoInfo = DtoItemMapper.itemInfoToDtoWithoutUsers(itemRepository.findByIdWithComments(itemId)
                .orElseThrow(() -> new EntityNotFoundException("item по id - " + itemId + " не найден2")));
        return dtoInfo;
    }

    public List<ItemDto> search(Long userId, String text, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user по id - " + userId + " не найден"));
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository
                .findAllByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingAndAvailableTrue(text.toLowerCase(), text.toLowerCase(), pageRequest)
                .map(DtoItemMapper::itemToDtoWithoutUsers).getContent();
    }

    public CommentTest addComment(Long userId, Long itemId, CommentDto commentDto) {
        LocalDateTime now = LocalDateTime.now();
        Comment comment = DtoCommentMapper.dtoToItem(commentDto);
        comment.setAuthor(userRepository.findById(userId)
                .orElseThrow(() -> new BadRequest("user по id - " + userId + " не найден")));
        comment.setItem(itemRepository.findByIdAndHaveBookingsByUserId(itemId, userId, BookingStatus.APPROVED, now)
                .orElseThrow(() -> new BadRequest("item по id - " + itemId + " не найден")));
        comment = commentRepository.save(comment);
        return commentRepository.findByIdAndAuthorId(comment.getId(), userId);
    }

    public void delete(Long userId, Long itemId) {
        itemRepository.findByIdAndWithUser(itemId, userId)
                .orElseThrow(() -> new EntityNotFoundException("user - " + userId + " не владеет item - " + itemId));
        itemRepository.deleteById(itemId);
    }
}
