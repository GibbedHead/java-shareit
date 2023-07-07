package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.RequestAddBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.booking.*;
import ru.practicum.shareit.exception.model.BookingNotFoundException;
import ru.practicum.shareit.exception.model.ItemNotFoundException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void save_whenEndTimeBeforeStartTime_thenBookingDateException() {
        RequestAddBookingDto requestAddBookingDto = new RequestAddBookingDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().minusHours(1)
        );
        final BookingDateException exception = assertThrows(
                BookingDateException.class,
                () -> bookingService.save(1L, requestAddBookingDto)
        );
        assertThat("Start date must be before end date", equalTo(exception.getMessage()));
    }

    @Test
    void save_whenEndTimeEqualsStartTime_thenBookingDateException() {
        LocalDateTime now = LocalDateTime.now();
        RequestAddBookingDto requestAddBookingDto = new RequestAddBookingDto(
                1L,
                now,
                now
        );
        final BookingDateException exception = assertThrows(
                BookingDateException.class,
                () -> bookingService.save(1L, requestAddBookingDto)
        );
        assertThat("Start date must be before end date", equalTo(exception.getMessage()));
    }

    @Test
    void save_whenInvalidItem_thenItemNotFoundException() {
        RequestAddBookingDto requestAddBookingDto = new RequestAddBookingDto(
                99L,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1)
        );
        when(itemRepository.findById(99L))
                .thenReturn(Optional.empty());
        final ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> bookingService.save(1L, requestAddBookingDto)
        );
        assertThat("Item id=99 not found", equalTo(exception.getMessage()));
    }

    @Test
    void save_whenOwnerBookingOwnItem_thenBookingOwnItemException() {
        RequestAddBookingDto requestAddBookingDto = new RequestAddBookingDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1)
        );
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                1L,
                null
        );
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        final BookingOwnItemException exception = assertThrows(
                BookingOwnItemException.class,
                () -> bookingService.save(1L, requestAddBookingDto)
        );
        assertThat("Operation is not supported", equalTo(exception.getMessage()));
    }

    @Test
    void save_whenBookingNotAvailableItem_thenBookingItemNotAvailableException() {
        RequestAddBookingDto requestAddBookingDto = new RequestAddBookingDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1)
        );
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                false,
                2L,
                null
        );
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        final BookingItemNotAvailableException exception = assertThrows(
                BookingItemNotAvailableException.class,
                () -> bookingService.save(1L, requestAddBookingDto)
        );
        assertThat("Item id=1 is not available", equalTo(exception.getMessage()));
    }

    @Test
    void save_whenBookingFromWrongUser_thenUserNotFoundException() {
        Long wrongUserId = 100L;
        RequestAddBookingDto requestAddBookingDto = new RequestAddBookingDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1)
        );
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                2L,
                null
        );

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(wrongUserId))
                .thenReturn(Optional.empty());
        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.save(wrongUserId, requestAddBookingDto)
        );
        assertThat(String.format("User id=%d not found", wrongUserId), equalTo(exception.getMessage()));
    }

    @Test
    void save_whenInvoke_thenReturnDto() {
        LocalDateTime now = LocalDateTime.now();
        RequestAddBookingDto requestAddBookingDto = new RequestAddBookingDto(
                1L,
                now,
                now.plusHours(1)
        );
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                2L,
                null
        );
        User user = new User(
                1L,
                "User1",
                "user1@domain.com"
        );
        Booking savedBooking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.WAITING
        );
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(savedBooking);
        ResponseBookingDto responseBookingDto = bookingService.save(1L, requestAddBookingDto);
        assertThat(1L, equalTo(responseBookingDto.getId()));
        assertThat(BookingStatus.WAITING, equalTo(responseBookingDto.getStatus()));
    }

    @Test
    void approve_whenInvalidBookingId_thenBookingNotFoundException() {
        Long wrongBookingId = 100L;
        when(bookingRepository.findById(wrongBookingId))
                .thenReturn(Optional.empty());
        final BookingNotFoundException exception = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.approve(wrongBookingId, wrongBookingId, true)
        );
        assertThat(String.format("Booking id=%d not found", wrongBookingId), equalTo(exception.getMessage()));
    }

    @Test
    void approve_whenInvalidUser_thenUserNotFoundException() {
        Long wrongUserId = 100L;
        LocalDateTime now = LocalDateTime.now();
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                2L,
                null
        );
        User user = new User(
                1L,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.WAITING
        );

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(wrongUserId))
                .thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.approve(wrongUserId, 1L, true)
        );

        assertThat(String.format("User id=%d not found", wrongUserId), equalTo(exception.getMessage()));
    }

    @Test
    void approve_whenUserIsNotItemOwner_thenBookingItemNotAvailableException() {
        LocalDateTime now = LocalDateTime.now();
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                2L,
                null
        );
        User user = new User(
                1L,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.WAITING
        );
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        Long userId = 1L;
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        final BookingNotOwnerOperationException exception = assertThrows(
                BookingNotOwnerOperationException.class,
                () -> bookingService.approve(userId, 1L, true)
        );
        assertThat(String.format("User id=%d is not item owner", userId), equalTo(exception.getMessage()));
    }

    @Test
    void approve_whenUserIsNotBookingOwner_thenBookingApprovingOwnBookingException() {
        LocalDateTime now = LocalDateTime.now();
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                1L,
                null
        );
        User user = new User(
                1L,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.WAITING
        );
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        Long userId = 1L;
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        final BookingApprovingOwnBookingException exception = assertThrows(
                BookingApprovingOwnBookingException.class,
                () -> bookingService.approve(userId, 1L, true)
        );
        assertThat("Operation is not supported", equalTo(exception.getMessage()));
    }

    @Test
    void approve_whenAlreadyApproved_thenBookingAlreadyApprovedException() {
        LocalDateTime now = LocalDateTime.now();
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                2L,
                null
        );
        User user = new User(
                1L,
                "User1",
                "user1@domain.com"
        );
        User user2 = new User(
                2L,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.APPROVED
        );
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        Long userId = 2L;
        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        final BookingAlreadyApprovedException exception = assertThrows(
                BookingAlreadyApprovedException.class,
                () -> bookingService.approve(userId, 1L, true)
        );
        assertThat("Operation is not supported", equalTo(exception.getMessage()));
    }

    @Test
    void approve_whenApprove_thenReturnApprovedDto() {
        LocalDateTime now = LocalDateTime.now();
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                2L,
                null
        );
        User user = new User(
                1L,
                "User1",
                "user1@domain.com"
        );
        User user2 = new User(
                2L,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.WAITING
        );
        Booking apprivedBooking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.APPROVED
        );
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        Long userId = 2L;
        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(apprivedBooking);
        ResponseBookingDto responseBookingDto = bookingService.approve(userId, 1L, true);
        assertThat(BookingStatus.APPROVED, equalTo(responseBookingDto.getStatus()));
    }

    @Test
    void approve_whenReject_thenReturnRejectedDto() {
        LocalDateTime now = LocalDateTime.now();
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                2L,
                null
        );
        User user = new User(
                1L,
                "User1",
                "user1@domain.com"
        );
        User user2 = new User(
                2L,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.WAITING
        );
        Booking apprivedBooking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.REJECTED
        );
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        Long userId = 2L;
        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        when(bookingRepository.save(apprivedBooking))
                .thenReturn(apprivedBooking);
        ResponseBookingDto responseBookingDto = bookingService.approve(userId, 1L, false);
        assertThat(BookingStatus.REJECTED, equalTo(responseBookingDto.getStatus()));
    }

    @Test
    void findById_whenWrongBooking_thenBookingNotFoundException() {
        Long wrongBookingId = 100L;
        when(bookingRepository.findById(wrongBookingId))
                .thenReturn(Optional.empty());
        final BookingNotFoundException exception = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.findById(wrongBookingId, wrongBookingId)
        );
        assertThat(String.format("Booking id=%d not found", wrongBookingId), equalTo(exception.getMessage()));
    }

    @Test
    void findById_whenNotBookingOwnerButItemOwner_thenReturnDto() {
        LocalDateTime now = LocalDateTime.now();
        Long itemOwnerId = 2L;
        Long bookingOwnerId = 1L;
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                itemOwnerId,
                null
        );
        User user = new User(
                bookingOwnerId,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.WAITING
        );
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        ResponseBookingDto responseBookingDto = bookingService.findById(itemOwnerId, 1L);
        assertThat(1L, equalTo(responseBookingDto.getId()));
    }

    @Test
    void findById_whenNotItemOwnerButBookingOwner_thenReturnDto() {
        LocalDateTime now = LocalDateTime.now();
        Long itemOwnerId = 2L;
        Long bookingOwnerId = 1L;
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                itemOwnerId,
                null
        );
        User user = new User(
                bookingOwnerId,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.WAITING
        );
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        ResponseBookingDto responseBookingDto = bookingService.findById(bookingOwnerId, 1L);
        assertThat(1L, equalTo(responseBookingDto.getId()));
    }

    @Test
    void findById_whenNotItemOwnerAndNotBookingOwner_thenBookingNotOwnerOperationException() {
        LocalDateTime now = LocalDateTime.now();
        Long itemOwnerId = 2L;
        Long bookingOwnerId = 1L;
        Long wrongUserId = 100L;
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                itemOwnerId,
                null
        );
        User user = new User(
                bookingOwnerId,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.WAITING
        );
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        final BookingNotOwnerOperationException exception = assertThrows(
                BookingNotOwnerOperationException.class,
                () -> bookingService.findById(wrongUserId, 1L)
        );
        assertThat("Operation is not supported", equalTo(exception.getMessage()));
    }



    @Test
    void findByUserIdAndState_whenInvalidUserId_thenUserNotFoundException() {
        Long wrongUserId = 100L;
        String stringState = "ALL";
        when(userRepository.findById(wrongUserId))
                .thenReturn(Optional.empty());
        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.findByUserIdAndState(wrongUserId, stringState, 0, 20)
        );
        assertThat(String.format("User id=%d not found", wrongUserId), equalTo(exception.getMessage()));
    }

    @Test
    void findByUserIdAndState_whenInvoke_thenDtosList() {
        LocalDateTime now = LocalDateTime.now();
        Long itemOwnerId = 2L;
        Long bookingOwnerId = 1L;
        String stringState = "ALL";
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                itemOwnerId,
                null
        );
        User user = new User(
                bookingOwnerId,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.APPROVED
        );
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findByBooker_IdOrderByEndDesc(1L, PageRequest.of(0, 20)))
                .thenReturn(List.of(booking));
        List<ResponseBookingDto> responseBookingDtos = bookingService.findByUserIdAndState(
                bookingOwnerId,
                stringState,
                0,
                20
        );
        assertThat(1L, equalTo(responseBookingDtos.get(0).getId()));
    }

    @Test
    void findByUserIdAndState_whenStateCurrent_thenReturnDtosList() {
        LocalDateTime now = LocalDateTime.now();
        Long itemOwnerId = 2L;
        Long bookingOwnerId = 1L;
        String stringState = "CURRENT";
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                itemOwnerId,
                null
        );
        User user = new User(
                bookingOwnerId,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.APPROVED
        );
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(
                bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(
                        any(),
                        any(),
                        any(),
                        any()
                )
        )
                .thenReturn(List.of(booking));
        List<ResponseBookingDto> responseBookingDtos = bookingService.findByUserIdAndState(
                bookingOwnerId,
                stringState,
                0,
                20
        );
        assertThat(1L, equalTo(responseBookingDtos.get(0).getId()));
    }

    @Test
    void findByUserIdAndState_whenPast_thenReturnDtosList() {
        LocalDateTime now = LocalDateTime.now();
        Long itemOwnerId = 2L;
        Long bookingOwnerId = 1L;
        String stringState = "PAST";
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                itemOwnerId,
                null
        );
        User user = new User(
                bookingOwnerId,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.APPROVED
        );
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(
                bookingRepository.findByBooker_IdAndEndIsBeforeOrderByEndDesc(
                        any(),
                        any(),
                        any()
                )
        )
                .thenReturn(List.of(booking));
        List<ResponseBookingDto> responseBookingDtos = bookingService.findByUserIdAndState(
                bookingOwnerId,
                stringState,
                0,
                20
        );
        assertThat(1L, equalTo(responseBookingDtos.get(0).getId()));
    }

    @Test
    void findByUserIdAndState_whenFuture_thenReturnDtosList() {
        LocalDateTime now = LocalDateTime.now();
        Long itemOwnerId = 2L;
        Long bookingOwnerId = 1L;
        String stringState = "FUTURE";
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                itemOwnerId,
                null
        );
        User user = new User(
                bookingOwnerId,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.APPROVED
        );
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(
                bookingRepository.findByBooker_IdAndStartIsAfterOrderByEndDesc(
                        any(),
                        any(),
                        any()
                )
        )
                .thenReturn(List.of(booking));
        List<ResponseBookingDto> responseBookingDtos = bookingService.findByUserIdAndState(
                bookingOwnerId,
                stringState,
                0,
                20
        );
        assertThat(1L, equalTo(responseBookingDtos.get(0).getId()));
    }

    @Test
    void findByUserIdAndState_whenWaiting_thenReturnDtosList() {
        LocalDateTime now = LocalDateTime.now();
        Long itemOwnerId = 2L;
        Long bookingOwnerId = 1L;
        String stringState = "WAITING";
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                itemOwnerId,
                null
        );
        User user = new User(
                bookingOwnerId,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.WAITING
        );
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(
                bookingRepository.findByBooker_IdAndStatusOrderByEndDesc(
                        any(),
                        any(),
                        any()
                )
        )
                .thenReturn(List.of(booking));
        List<ResponseBookingDto> responseBookingDtos = bookingService.findByUserIdAndState(
                bookingOwnerId,
                stringState,
                0,
                20
        );
        assertThat(1L, equalTo(responseBookingDtos.get(0).getId()));
    }

    @Test
    void findByUserIdAndState_whenRejected_thenReturnDtosList() {
        LocalDateTime now = LocalDateTime.now();
        Long itemOwnerId = 2L;
        Long bookingOwnerId = 1L;
        String stringState = "REJECTED";
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                itemOwnerId,
                null
        );
        User user = new User(
                bookingOwnerId,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.REJECTED
        );
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(
                bookingRepository.findByBooker_IdAndStatusOrderByEndDesc(
                        any(),
                        any(),
                        any()
                )
        )
                .thenReturn(List.of(booking));
        List<ResponseBookingDto> responseBookingDtos = bookingService.findByUserIdAndState(
                bookingOwnerId,
                stringState,
                0,
                20
        );
        assertThat(1L, equalTo(responseBookingDtos.get(0).getId()));
    }

    @Test
    void findByItemOwner_whenInvalidUserId_thenUserNotFoundException() {
        Long wrongUserId = 100L;
        String stringState = "ALL";
        when(userRepository.findById(wrongUserId))
                .thenReturn(Optional.empty());
        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.findByItemOwner(wrongUserId, stringState, 0, 20)
        );
        assertThat(String.format("User id=%d not found", wrongUserId), equalTo(exception.getMessage()));
    }

    @Test
    void findByItemOwner_whenAll_thenReturnDtosList() {
        LocalDateTime now = LocalDateTime.now();
        Long itemOwnerId = 2L;
        Long bookingOwnerId = 1L;
        String stringState = "ALL";
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                itemOwnerId,
                null
        );
        User user = new User(
                bookingOwnerId,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.APPROVED
        );
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByOwner(1L, PageRequest.of(0, 20)))
                .thenReturn(List.of(booking));
        List<ResponseBookingDto> responseBookingDtos = bookingService.findByItemOwner(
                bookingOwnerId,
                stringState,
                0,
                20
        );
        assertThat(1L, equalTo(responseBookingDtos.get(0).getId()));
    }

    @Test
    void findByItemOwner_whenCurrent_thenReturnDtosList() {
        LocalDateTime now = LocalDateTime.now();
        Long itemOwnerId = 2L;
        Long bookingOwnerId = 1L;
        String stringState = "CURRENT";
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                itemOwnerId,
                null
        );
        User user = new User(
                bookingOwnerId,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.APPROVED
        );
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(
                bookingRepository.findAllByOwnerCurrent(
                        any(),
                        any(),
                        any()
                )
        )
                .thenReturn(List.of(booking));
        List<ResponseBookingDto> responseBookingDtos = bookingService.findByItemOwner(
                bookingOwnerId,
                stringState,
                0,
                20
        );
        assertThat(1L, equalTo(responseBookingDtos.get(0).getId()));
    }

    @Test
    void findByItemOwner_whenPast_thenReturnDtosList() {
        LocalDateTime now = LocalDateTime.now();
        Long itemOwnerId = 2L;
        Long bookingOwnerId = 1L;
        String stringState = "PAST";
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                itemOwnerId,
                null
        );
        User user = new User(
                bookingOwnerId,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.APPROVED
        );
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(
                bookingRepository.findAllByOwnerPast(
                        any(),
                        any(),
                        any()
                )
        )
                .thenReturn(List.of(booking));
        List<ResponseBookingDto> responseBookingDtos = bookingService.findByItemOwner(
                bookingOwnerId,
                stringState,
                0,
                20
        );
        assertThat(1L, equalTo(responseBookingDtos.get(0).getId()));
    }

    @Test
    void findByItemOwner_whenFuture_thenReturnDtosList() {
        LocalDateTime now = LocalDateTime.now();
        Long itemOwnerId = 2L;
        Long bookingOwnerId = 1L;
        String stringState = "FUTURE";
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                itemOwnerId,
                null
        );
        User user = new User(
                bookingOwnerId,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.APPROVED
        );
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(
                bookingRepository.findAllByOwnerFuture(
                        any(),
                        any(),
                        any()
                )
        )
                .thenReturn(List.of(booking));
        List<ResponseBookingDto> responseBookingDtos = bookingService.findByItemOwner(
                bookingOwnerId,
                stringState,
                0,
                20
        );
        assertThat(1L, equalTo(responseBookingDtos.get(0).getId()));
    }

    @Test
    void findByItemOwner_whenWaiting_thenReturnDtosList() {
        LocalDateTime now = LocalDateTime.now();
        Long itemOwnerId = 2L;
        Long bookingOwnerId = 1L;
        String stringState = "WAITING";
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                itemOwnerId,
                null
        );
        User user = new User(
                bookingOwnerId,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.WAITING
        );
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(
                bookingRepository.findAllByOwnerAndStatus(
                        any(),
                        any(),
                        any()
                )
        )
                .thenReturn(List.of(booking));
        List<ResponseBookingDto> responseBookingDtos = bookingService.findByItemOwner(
                bookingOwnerId,
                stringState,
                0,
                20
        );
        assertThat(1L, equalTo(responseBookingDtos.get(0).getId()));
    }

    @Test
    void findByItemOwner_whenRejected_thenReturnDtosList() {
        LocalDateTime now = LocalDateTime.now();
        Long itemOwnerId = 2L;
        Long bookingOwnerId = 1L;
        String stringState = "REJECTED";
        Item item = new Item(
                1L,
                "Item",
                "Desc",
                true,
                itemOwnerId,
                null
        );
        User user = new User(
                bookingOwnerId,
                "User1",
                "user1@domain.com"
        );
        Booking booking = new Booking(
                1L,
                now,
                now.plusHours(1),
                item,
                user,
                BookingStatus.REJECTED
        );
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(
                bookingRepository.findAllByOwnerAndStatus(
                        any(),
                        any(),
                        any()
                )
        )
                .thenReturn(List.of(booking));
        List<ResponseBookingDto> responseBookingDtos = bookingService.findByItemOwner(
                bookingOwnerId,
                stringState,
                0,
                20
        );
        assertThat(1L, equalTo(responseBookingDtos.get(0).getId()));
    }

}