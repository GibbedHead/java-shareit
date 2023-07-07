package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.RequestAddBookingDto;
import ru.practicum.shareit.booking.model.BookingTestGenerator;
import ru.practicum.shareit.item.dto.RequestAddCommentDto;
import ru.practicum.shareit.item.dto.RequestAddItemDto;
import ru.practicum.shareit.item.model.CommentTestGenerator;
import ru.practicum.shareit.item.model.ItemTestGenerator;
import ru.practicum.shareit.user.dto.RequestAddUserDto;
import ru.practicum.shareit.user.model.UserTestGenerator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ShareItServerTests {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testValidUser() {
        RequestAddUserDto validUser = UserTestGenerator.getUser1();
        Set<ConstraintViolation<RequestAddUserDto>> validViolations = validator.validate(validUser);
        assertEquals(0, validViolations.size());
    }

    @Test
    void testValidItem() {
        RequestAddItemDto validItem = ItemTestGenerator.getItem();
        Set<ConstraintViolation<RequestAddItemDto>> validViolations = validator.validate(validItem);
        assertEquals(0, validViolations.size());
    }

    @Test
    void bookingValidAdd() {
        RequestAddBookingDto requestAddBookingDto = BookingTestGenerator.getRequestAddBookingDto();
        Set<ConstraintViolation<RequestAddBookingDto>> availabilityViolations = validator.validate(requestAddBookingDto);
        assertEquals(0, availabilityViolations.size());

    }

    @Test
    void commentValidAdd() {
        RequestAddCommentDto requestAddCommentDto = CommentTestGenerator.getAddCommentDto();
        Set<ConstraintViolation<RequestAddCommentDto>> availabilityViolations = validator.validate(requestAddCommentDto);
        assertEquals(0, availabilityViolations.size());
    }
}
