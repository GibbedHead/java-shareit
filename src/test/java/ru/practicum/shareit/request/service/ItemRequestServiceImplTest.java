package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.model.RequestNotFoundException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.item.dto.ResponseItemForItemRequestDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestAddItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void save_whenValidUser_thenSaveAndReturnItemRequest() {
        Long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User()));
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(new ItemRequest(
                        1L,
                        "Request",
                        new User(),
                        LocalDateTime.now()
                ));

        ResponseItemRequestDto responseItemRequestDto = itemRequestService.save(userId, new RequestAddItemRequestDto());

        assertThat(1L, equalTo(responseItemRequestDto.getId()));
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void save_whenInvalidUser_thenUserNotFoundException() {
        Long userId = 1L;
        RequestAddItemRequestDto requestAddItemRequestDto = new RequestAddItemRequestDto("request");

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemRequestService.save(userId, requestAddItemRequestDto));
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void findByUserId_whenValidUser_thenReturnDtosListWithItems() {
        Long userId = 1L;
        User user = new User();

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(itemRequestRepository.findAllByRequestorIdOrderByIdDesc(userId))
                .thenReturn(List.of(
                        new ItemRequest(
                                1L,
                                "Request",
                                user,
                                LocalDateTime.now()
                        )
                ));
        when(itemService.getResponseItemRequestWithItemsDtoByRequestId(anyLong()))
                .thenReturn(List.of(new ResponseItemForItemRequestDto(
                        1L,
                        "Item",
                        "Desc",
                        true,
                        1L
                )));

        List<ResponseItemRequestWithItemsDto> withItemsDtos = itemRequestService.findByUserId(userId);

        assertThat(1, equalTo(withItemsDtos.size()));
        assertThat("Item", equalTo(withItemsDtos.get(0).getItems().get(0).getName()));
    }

    @Test
    void findByUserId_whenInvalidUser_thenUserNotFoundException() {
        Long userId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> itemRequestService.findByUserId(userId));
        verify(itemRequestRepository, never()).findAllByRequestorIdOrderByIdDesc(anyLong());
        verify(itemService, never()).getResponseItemRequestWithItemsDtoByRequestId(anyLong());
    }

    @Test
    void findAllNotOwned_whenValidUser_thenReturnDtosList() {
        Long userId = 1L;
        User user = new User();
        Pageable pageable = PageRequest.of(0, 20);

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(itemRequestRepository.findAllByRequestorIdNotOrderByIdDesc(userId, pageable))
                .thenReturn(List.of(
                        new ItemRequest(
                                1L,
                                "Request",
                                user,
                                LocalDateTime.now()
                        )
                ));
        when(itemService.getResponseItemRequestWithItemsDtoByRequestId(anyLong()))
                .thenReturn(List.of(new ResponseItemForItemRequestDto(
                        1L,
                        "Item",
                        "Desc",
                        true,
                        1L
                )));

        List<ResponseItemRequestWithItemsDto> allNotOwned = itemRequestService.findAllNotOwned(userId, 0, 20);

        assertThat(1, equalTo(allNotOwned.size()));
        assertThat("Item", equalTo(allNotOwned.get(0).getItems().get(0).getName()));
    }

    @Test
    void findAllNotOwned_whenInvalidUser_thenUserNotFoundException() {
        Long userId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> itemRequestService.findAllNotOwned(userId, 0, 20));
        verify(itemRequestRepository, never()).findAllByRequestorIdNotOrderByIdDesc(anyLong(), any(Pageable.class));
        verify(itemService, never()).getResponseItemRequestWithItemsDtoByRequestId(anyLong());
    }

    @Test
    void findById_whenValidUserAndRequest_thenReturnItemRequest() {
        Long userId = 1L;
        Long requestId = 1L;
        User requestor = new User();
        ItemRequest itemRequest = new ItemRequest(
                1L,
                "Request",
                requestor,
                LocalDateTime.now()
        );

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        when(itemService.getResponseItemRequestWithItemsDtoByRequestId(anyLong()))
                .thenReturn(List.of(new ResponseItemForItemRequestDto(
                        1L,
                        "Item",
                        "Desc",
                        true,
                        1L
                )));

        ResponseItemRequestWithItemsDto withItemsDto = itemRequestService.findById(userId, requestId);

        assertThat(1L, equalTo(withItemsDto.getId()));
        assertThat("Item", equalTo(withItemsDto.getItems().get(0).getName()));
    }

    @Test
    void findById_whenInvalidUser_thenUserNotFoundException() {
        Long userId = 1L;
        Long requestId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> itemRequestService.findById(userId, requestId));
        verify(itemRequestRepository, never()).findById(anyLong());
    }

    @Test
    void findById_whenInvalidRequest_thenRequestNotFoundException() {
        Long userId = 1L;
        Long requestId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(RequestNotFoundException.class, () -> itemRequestService.findById(userId, requestId));
    }
}