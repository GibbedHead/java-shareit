package ru.practicum.shareit.booking.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingTestGenerator;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemTestGenerator;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.maper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserTestGenerator;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@Sql(scripts = {"/schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryTest {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Test
    void add() {
        User user = userRepository.save(userMapper.addDtoToUser(UserTestGenerator.getUser1()));
        Item item = itemMapper.addDtoToItem(ItemTestGenerator.getItem());
        item.setOwnerId(user.getId());
        item = itemRepository.save(item);
        Booking booking = bookingMapper.addDtoToBooking(BookingTestGenerator.getRequestAddBookingDto());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        assertEquals(1L, savedBooking.getId());
    }
}