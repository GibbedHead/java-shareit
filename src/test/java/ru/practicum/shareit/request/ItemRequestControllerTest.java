package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.RequestAddItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController itemRequestController;

    private final Long userId = 1L;
    private final Long requestId = 1L;
    private final Integer from = 0;
    private final Integer size = 20;
    private final LocalDateTime now = LocalDateTime.now();
    private final String description = "Descr";
    private final ResponseItemRequestDto responseItemRequestDto = new ResponseItemRequestDto(
            requestId,
            description,
            now
    );
    private final ResponseItemRequestWithItemsDto responseItemRequestWithItemsDto = new ResponseItemRequestWithItemsDto(
            requestId,
            description,
            now,
            List.of()
    );
    private final List<ResponseItemRequestWithItemsDto> requestsWithItemDtos = List.of(responseItemRequestWithItemsDto);

    @Test
    void save_whenInvoke_thenReturnResponseDto() {
        RequestAddItemRequestDto requestAddItemRequestDto = new RequestAddItemRequestDto();
        when(itemRequestService.save(userId, requestAddItemRequestDto))
                .thenReturn(responseItemRequestDto);

        ResponseItemRequestDto saved = itemRequestController.save(userId, requestAddItemRequestDto);

        assertThat(1L, equalTo(saved.getId()));
    }

    @Test
    void findByUserId_whenInvoke_thenReturnResponseDtoList() {
        when(itemRequestService.findByUserId(userId))
                .thenReturn(requestsWithItemDtos);

        List<ResponseItemRequestWithItemsDto> withItemsDtos = itemRequestController.findByUserId(userId);

        assertThat(1L, equalTo(withItemsDtos.get(0).getId()));
    }

    @Test
    void findAllNotOwned_whenInvoke_thenReturnResponseDtoList() {
        when(itemRequestService.findAllNotOwned(userId, from, size))
                .thenReturn(requestsWithItemDtos);

        List<ResponseItemRequestWithItemsDto> withItemsDtos = itemRequestController.findAllNotOwned(userId, from, size);

        assertThat(1L, equalTo(withItemsDtos.get(0).getId()));
    }

    @Test
    void findById_whenInvoke_thenReturnResponseDto() {
        when(itemRequestService.findById(userId, requestId))
                .thenReturn(responseItemRequestWithItemsDto);

        ResponseItemRequestWithItemsDto withItemsDto = itemRequestController.findById(userId, requestId);

        assertThat(1L, equalTo(withItemsDto.getId()));
    }
}