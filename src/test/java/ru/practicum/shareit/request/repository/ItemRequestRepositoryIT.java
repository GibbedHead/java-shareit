package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class ItemRequestRepositoryIT {
    private final Pageable pageable = PageRequest.of(0, 20);

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void findAllByRequestorIdOrderByIdDesc() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByIdDesc(1L);

        assertThat(1, equalTo(requests.size()));
        assertThat(1L, equalTo(requests.get(0).getId()));
    }

    @Test
    void findAllByRequestorIdNotOrderByIdDesc() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdNotOrderByIdDesc(1L, pageable);

        assertThat(0, equalTo(requests.size()));
    }
}