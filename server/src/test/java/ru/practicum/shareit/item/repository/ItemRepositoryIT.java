package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class ItemRepositoryIT {
    private final Pageable pageable = PageRequest.of(0, 20);

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findByOwnerIdOrderByIdAsc() {
        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(
                1L,
                pageable
        );

        assertThat(1, equalTo(items.size()));
        assertThat(1L, equalTo(items.get(0).getId()));
    }

    @Test
    void findAllByNameOrDescriptionContainingAndIsAvailableIgnoreCase() {
        List<Item> items = itemRepository.findAllByNameOrDescriptionContainingAndIsAvailableIgnoreCase(
                "ОтВерт",
                pageable
        );

        assertThat(1, equalTo(items.size()));
        assertThat(2L, equalTo(items.get(0).getId()));
    }

    @Test
    void findAllByRequestId() {
        List<Item> items = itemRepository.findAllByRequestId(1L);

        assertThat(1, equalTo(items.size()));
        assertThat(5L, equalTo(items.get(0).getId()));
    }
}