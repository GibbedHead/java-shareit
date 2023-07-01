package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.model.AccessBadRequestException;
import ru.practicum.shareit.exception.model.AccessNotFoundException;
import ru.practicum.shareit.exception.model.ItemNotFoundException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void save_whenValidUser_returnDto() {
        Long userId = 1L;
        Item itemToSave = new Item(
                null,
                "Item1",
                "Desc1",
                true,
                userId,
                null
        );
        Item itemSaved = new Item(
                1L,
                "Item1",
                "Desc1",
                true,
                userId,
                null
        );
        RequestAddItemDto requestAddItemDto = new RequestAddItemDto(
                "Item1",
                "Desc1",
                true,
                userId,
                null
        );

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(itemRepository.save(itemToSave))
                .thenReturn(itemSaved);

        ResponseItemDto responseItemDto = itemService.save(userId, requestAddItemDto);

        assertThat(requestAddItemDto.getName(), equalTo(responseItemDto.getName()));
        verify(itemRepository).save(itemToSave);
    }

    @Test
    void save_whenInvalidUser_thenUserNotFoundException() {
        Long userId = 100L;

        when(userRepository.existsById(userId))
                .thenReturn(false);

        RequestAddItemDto requestAddItemDto = new RequestAddItemDto(
                "Item1",
                "Desc1",
                true,
                userId,
                null
        );

        assertThrows(
                UserNotFoundException.class,
                () -> itemService.save(userId, requestAddItemDto)
        );

        verify(itemRepository, never()).save(new Item());
    }

    @Test
    void findById_whenInvalidItemId_thenItemNotFoundException() {
        Long itemId = 100L;
        Long userId = 1L;

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.findById(userId, itemId));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void findById_whenUserIsOwner_thenReturnDtoWithNextLastBookings() {
        Long userId = 1L;
        Long itemId = 1L;
        Long ownerId = userId;
        Item itemFound = new Item(
                itemId,
                "Item1",
                "Desc1",
                true,
                ownerId,
                null
        );
        User user = new User(userId, "Name", "e@mail.com");
        Booking nextBooking = new Booking(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                itemFound, user,
                BookingStatus.APPROVED
        );
        Booking lastBooking = new Booking(
                2L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                itemFound, user,
                BookingStatus.APPROVED);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(itemFound));
        when(bookingRepository.findFirst1ByItem_IdAndStartLessThanAndStatusOrderByEndDesc(
                anyLong(),
                any(LocalDateTime.class),
                any(BookingStatus.class)
        ))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository.findFirst1ByItem_IdAndStartGreaterThanAndStatusOrderByStartAsc(
                anyLong(),
                any(LocalDateTime.class),
                any(BookingStatus.class)
        ))
                .thenReturn(List.of(nextBooking));

        ResponseItemWithCommentsDto itemWithCommentsDto = itemService.findById(userId, itemId);

        assertThat(itemWithCommentsDto.getNextBooking(), is(notNullValue()));
        assertThat(itemWithCommentsDto.getLastBooking(), is(notNullValue()));

    }

    @Test
    void findById_whenUserIsNotOwner_thenReturnDtoWithoutNextLastBookings() {
        Long userId = 1L;
        Long itemId = 1L;
        Long ownerId = 2L;
        Item itemFound = new Item(
                itemId,
                "Item1",
                "Desc1",
                true,
                ownerId,
                null
        );

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(itemFound));


        ResponseItemWithCommentsDto itemWithCommentsDto = itemService.findById(userId, itemId);

        assertThat(itemWithCommentsDto.getNextBooking(), is(nullValue()));
        assertThat(itemWithCommentsDto.getLastBooking(), is(nullValue()));

    }

    @Test
    void findById_whenHaveComment_thenResponseWithComments() {
        Long userId = 1L;
        Long itemId = 1L;
        User user = new User(userId, "Name", "e@mail.com");
        Item itemFound = new Item(
                itemId,
                "Item1",
                "Desc1",
                true,
                userId,
                null
        );
        Comment comment = new Comment(
                1L,
                "Comment",
                itemFound,
                user,
                LocalDateTime.now()
        );

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(itemFound));
        when(commentRepository.findByItem_Id(anyLong()))
                .thenReturn(List.of(comment));

        ResponseItemWithCommentsDto itemWithCommentsDto = itemService.findById(userId, itemId);

        assertThat(1, equalTo(itemWithCommentsDto.getComments().size()));

    }

    @Test
    void findById_whenUserIsOwnerAndHasComments_thenReturnDtoWithNextLastBookingsAndComments() {
        Long userId = 1L;
        Long itemId = 1L;
        Long ownerId = userId;
        Item itemFound = new Item(
                itemId,
                "Item1",
                "Desc1",
                true,
                ownerId,
                null
        );
        User user = new User(userId, "Name", "e@mail.com");
        Booking nextBooking = new Booking(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                itemFound, user,
                BookingStatus.APPROVED
        );
        Booking lastBooking = new Booking(
                2L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                itemFound, user,
                BookingStatus.APPROVED);
        Comment comment = new Comment(
                1L,
                "Comment",
                itemFound,
                user,
                LocalDateTime.now()
        );

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(itemFound));
        when(bookingRepository.findFirst1ByItem_IdAndStartLessThanAndStatusOrderByEndDesc(
                anyLong(),
                any(LocalDateTime.class),
                any(BookingStatus.class)
        ))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository.findFirst1ByItem_IdAndStartGreaterThanAndStatusOrderByStartAsc(
                anyLong(),
                any(LocalDateTime.class),
                any(BookingStatus.class)
        ))
                .thenReturn(List.of(nextBooking));
        when(commentRepository.findByItem_Id(anyLong()))
                .thenReturn(List.of(comment));

        ResponseItemWithCommentsDto itemWithCommentsDto = itemService.findById(userId, itemId);

        assertThat(1L, equalTo(itemWithCommentsDto.getNextBooking().getId()));
        assertThat(2L, equalTo(itemWithCommentsDto.getLastBooking().getId()));
        assertThat(1, equalTo(itemWithCommentsDto.getComments().size()));

    }

    @Test
    void findByUserId_whenInvoked_thenReturnListOfDtoWBookingsAndComments() {
        Long userId = 1L;
        Long itemId = 1L;
        Long ownerId = userId;
        Integer from = 0;
        Integer size = 20;
        Pageable pageable = PageRequest.of(from / size, size);
        Item itemFound = new Item(
                itemId,
                "Item1",
                "Desc1",
                true,
                ownerId,
                null
        );
        User user = new User(userId, "Name", "e@mail.com");
        Booking nextBooking = new Booking(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                itemFound, user,
                BookingStatus.APPROVED
        );
        Booking lastBooking = new Booking(
                2L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                itemFound, user,
                BookingStatus.APPROVED);
        Comment comment = new Comment(
                1L,
                "Comment",
                itemFound,
                user,
                LocalDateTime.now()
        );

        when(itemRepository.findByOwnerIdOrderByIdAsc(userId, pageable))
                .thenReturn(List.of(itemFound));
        when(bookingRepository.findFirst1ByItem_IdAndStartLessThanAndStatusOrderByEndDesc(
                anyLong(),
                any(LocalDateTime.class),
                any(BookingStatus.class)
        ))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository.findFirst1ByItem_IdAndStartGreaterThanAndStatusOrderByStartAsc(
                anyLong(),
                any(LocalDateTime.class),
                any(BookingStatus.class)
        ))
                .thenReturn(List.of(nextBooking));
        when(commentRepository.findByItem_Id(anyLong()))
                .thenReturn(List.of(comment));

        List<ResponseItemWithCommentsDto> itemWithCommentsDtos = itemService.findByUserId(userId, from, size);

        assertThat(1, equalTo(itemWithCommentsDtos.size()));
        assertThat(1L, equalTo(itemWithCommentsDtos.get(0).getNextBooking().getId()));
        assertThat(2L, equalTo(itemWithCommentsDtos.get(0).getLastBooking().getId()));
        assertThat(1, equalTo(itemWithCommentsDtos.get(0).getComments().size()));
    }

    @Test
    void update_whenInvalidItem_thenItemNotFoundException() {
        Long userId = 1L;
        Long itemId = 100L;
        RequestUpdateItemDto itemDto = new RequestUpdateItemDto(
                itemId,
                "name",
                "desc",
                true,
                null,
                null
        );

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.update(userId, itemId, itemDto));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update_whenUserIsNotOwner_thenAccessNotFoundException() {
        Long userId = 100L;
        Long ownerId = 1L;
        Long itemId = 1L;
        RequestUpdateItemDto itemDto = new RequestUpdateItemDto(
                itemId,
                "name",
                "desc",
                true,
                null,
                null
        );
        Item itemFound = new Item(
                itemId,
                "Item1",
                "Desc1",
                true,
                ownerId,
                null
        );

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(itemFound));


        assertThrows(AccessNotFoundException.class, () -> itemService.update(userId, itemId, itemDto));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update_whenValidItemUserIsOwner_thenReturnUpdatedItem() {
        Long userId = 1L;
        Long ownerId = 1L;
        Long itemId = 1L;
        RequestUpdateItemDto itemDto = new RequestUpdateItemDto(
                itemId,
                "name",
                "desc",
                true,
                null,
                null
        );
        Item itemFound = new Item(
                itemId,
                "Item1",
                "Desc1",
                true,
                ownerId,
                null
        );
        Item itemUpdatedAndSaved = new Item(
                itemId,
                "name",
                "desc",
                true,
                ownerId,
                null
        );

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(itemFound));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(itemUpdatedAndSaved);

        ResponseItemDto responseItemDto = itemService.update(userId, itemId, itemDto);

        assertThat("name", equalTo(responseItemDto.getName()));
    }

    @Test
    void deleteById_whenInvalidItem_thenItemNotFoundException() {
        doThrow(EmptyResultDataAccessException.class).when(itemRepository).deleteById(anyLong());

        assertThrows(ItemNotFoundException.class, () -> itemService.deleteById(1L));
    }

    @Test
    void deleteById_whenValidItem_thenInvokeRepositoryDelete() {
        itemService.deleteById(1L);

        verify(itemRepository, atMostOnce()).deleteById(1L);
    }

    @Test
    void findByNameOrDescription_whenBlankSearch_thenReturnEmptyList() {
        List<ResponseItemDto> responseItemDtos = itemService.findByNameOrDescription("", 0, 20);

        assertThat(0, equalTo(responseItemDtos.size()));
        verify(itemRepository, never()).findAllByNameOrDescriptionContainingAndIsAvailableIgnoreCase(any(), any());
    }

    @Test
    void findByNameOrDescription_whenValidSearch_thenReturnDtosList() {
        Integer from = 0;
        Integer size = 20;
        Pageable pageable = PageRequest.of(from / size, size);
        Item itemFound = new Item(
                1L,
                "Item1",
                "Desc1",
                true,
                1L,
                null
        );

        when(itemRepository.findAllByNameOrDescriptionContainingAndIsAvailableIgnoreCase("Item1", pageable))
                .thenReturn(List.of(itemFound));

        List<ResponseItemDto> responseItemDtos = itemService.findByNameOrDescription("Item1", from, size);

        assertThat(1, equalTo(responseItemDtos.size()));
        assertThat("Item1", equalTo(responseItemDtos.get(0).getName()));
    }

    @Test
    void saveComment_whenValidUserAndItemAndUserIsBooker_thenReturnCommentDto() {
        Long userId = 1L;
        Long itemId = 1L;
        Long ownerId = 1L;
        Item itemFound = new Item(
                itemId,
                "Item1",
                "Desc1",
                true,
                ownerId,
                null
        );
        User user = new User(userId, "Name", "e@mail.com");
        RequestAddCommentDto requestAddCommentDto = new RequestAddCommentDto("Good item");
        Comment savedComment = new Comment(
                null,
                "Good item",
                itemFound,
                user,
                LocalDateTime.now()
        );

        when(commentRepository.save(savedComment))
                .thenReturn(savedComment);
        when(bookingRepository.findFirst1ByItem_IdAndBooker_IdAndStatusAndEndBefore(
                anyLong(),
                anyLong(),
                any(BookingStatus.class),
                any(LocalDateTime.class)
        ))
                .thenReturn(List.of(new Booking()));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(itemFound));

        ResponseCommentDto commentDto = itemService.saveComment(userId, itemId, requestAddCommentDto);

        assertThat("Name", equalTo(commentDto.getAuthorName()));
        assertThat("Good item", equalTo(commentDto.getText()));
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void saveComment_whenUserIsNotBooker_thenAccessBadRequestException() {
        Long userId = 1L;
        Long itemId = 1L;
        RequestAddCommentDto requestAddCommentDto = new RequestAddCommentDto("Good item");

        when(bookingRepository.findFirst1ByItem_IdAndBooker_IdAndStatusAndEndBefore(
                anyLong(),
                anyLong(),
                any(BookingStatus.class),
                any(LocalDateTime.class)
        ))
                .thenReturn(List.of());

        assertThrows(AccessBadRequestException.class, () -> itemService.saveComment(userId, itemId, requestAddCommentDto));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void saveComment_whenInvalidUser_thenUserNotFoundException() {
        Long userId = 1L;
        Long itemId = 1L;
        RequestAddCommentDto requestAddCommentDto = new RequestAddCommentDto("Good item");

        when(bookingRepository.findFirst1ByItem_IdAndBooker_IdAndStatusAndEndBefore(
                anyLong(),
                anyLong(),
                any(BookingStatus.class),
                any(LocalDateTime.class)
        ))
                .thenReturn(List.of(new Booking()));
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.saveComment(userId, itemId, requestAddCommentDto));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void saveComment_whenInvalidItem_thenItemNotFoundException() {
        Long userId = 1L;
        Long itemId = 1L;
        User user = new User(userId, "Name", "e@mail.com");
        RequestAddCommentDto requestAddCommentDto = new RequestAddCommentDto("Good item");

        when(bookingRepository.findFirst1ByItem_IdAndBooker_IdAndStatusAndEndBefore(
                anyLong(),
                anyLong(),
                any(BookingStatus.class),
                any(LocalDateTime.class)
        ))
                .thenReturn(List.of(new Booking()));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.saveComment(userId, itemId, requestAddCommentDto));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void getResponseItemRequestWithItemsDtoByRequestId_whenInvoked_thenReturnDtosList() {
        Long requestId = 1L;
        Long itemId = 1L;
        Long ownerId = 1L;
        Item itemFound = new Item(
                itemId,
                "Item1",
                "Desc1",
                true,
                ownerId,
                requestId
        );

        when(itemRepository.findAllByRequestId(requestId))
                .thenReturn(List.of(itemFound));

        List<ResponseItemForItemRequestDto> itemsDtoByRequestId =
                itemService.getResponseItemRequestWithItemsDtoByRequestId(requestId);

        assertThat(1, equalTo(itemsDtoByRequestId.size()));
        assertThat(requestId, equalTo(itemsDtoByRequestId.get(0).getRequestId()));
    }
}