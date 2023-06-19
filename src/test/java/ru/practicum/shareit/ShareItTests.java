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
class ShareItTests {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testValidUser() {
        RequestAddUserDto validUser = UserTestGenerator.getUser();
        Set<ConstraintViolation<RequestAddUserDto>> validViolations = validator.validate(validUser);
        assertEquals(0, validViolations.size());
    }

    @Test
    void requestUserNameShouldNotBeBlank() {
        RequestAddUserDto nullNameUser = UserTestGenerator.getNullNameUser();
        Set<ConstraintViolation<RequestAddUserDto>> nullViolations = validator.validate(nullNameUser);
        assertEquals(1, nullViolations.size());

        RequestAddUserDto emptyNameUser = UserTestGenerator.getEmptyNameUser();
        Set<ConstraintViolation<RequestAddUserDto>> emptyViolations = validator.validate(emptyNameUser);
        assertEquals(1, emptyViolations.size());
    }

    @Test
    void requestUserEmailShouldNotBeBlank() {
        RequestAddUserDto nullEmailUser = UserTestGenerator.getNullEmailUser();
        Set<ConstraintViolation<RequestAddUserDto>> nullViolations = validator.validate(nullEmailUser);
        assertEquals(1, nullViolations.size());

        RequestAddUserDto emptyEmailUser = UserTestGenerator.getEmptyEmailUser();
        Set<ConstraintViolation<RequestAddUserDto>> emptyViolations = validator.validate(emptyEmailUser);
        assertEquals(1, emptyViolations.size());
    }

    @Test
    void requestUserEmailShouldBeValidEmail() {
        RequestAddUserDto invalidEmailUser = UserTestGenerator.getInvalidEmailUser();
        Set<ConstraintViolation<RequestAddUserDto>> emailViolations = validator.validate(invalidEmailUser);
        assertEquals(1, emailViolations.size());
    }

    @Test
    void testValidItem() {
        RequestAddItemDto validItem = ItemTestGenerator.getItem();
        Set<ConstraintViolation<RequestAddItemDto>> validViolations = validator.validate(validItem);
        assertEquals(0, validViolations.size());
    }

    @Test
    void requestItemNameShouldNotBeBlank() {
        RequestAddItemDto nullNameItem = ItemTestGenerator.getNullNameItem();
        Set<ConstraintViolation<RequestAddItemDto>> nullViolations = validator.validate(nullNameItem);
        assertEquals(1, nullViolations.size());

        RequestAddItemDto emptyNameItem = ItemTestGenerator.getEmptyNameItem();
        Set<ConstraintViolation<RequestAddItemDto>> emptyViolations = validator.validate(emptyNameItem);
        assertEquals(1, emptyViolations.size());
    }

    @Test
    void requestItemDescriptionShouldNotBeBlank() {
        RequestAddItemDto nullDescriptionItem = ItemTestGenerator.getNullDescriptionIItem();
        Set<ConstraintViolation<RequestAddItemDto>> nullViolations = validator.validate(nullDescriptionItem);
        assertEquals(1, nullViolations.size());

        RequestAddItemDto emptyDescriptionItem = ItemTestGenerator.getEmptyDescriptionItem();
        Set<ConstraintViolation<RequestAddItemDto>> emptyViolations = validator.validate(emptyDescriptionItem);
        assertEquals(1, emptyViolations.size());
    }

    @Test
    void requestItemAvailabilityShouldBeNull() {
        RequestAddItemDto nullAvailabilityItem = ItemTestGenerator.getNullAvailabilityItem();
        Set<ConstraintViolation<RequestAddItemDto>> availabilityViolations = validator.validate(nullAvailabilityItem);
        assertEquals(1, availabilityViolations.size());
    }

    @Test
    void bookingValidAdd() {
        RequestAddBookingDto requestAddBookingDto = BookingTestGenerator.getRequestAddBookingDto();
        Set<ConstraintViolation<RequestAddBookingDto>> availabilityViolations = validator.validate(requestAddBookingDto);
        assertEquals(0, availabilityViolations.size());

    }

    @Test
    void bookingAddItemIdShouldNotBeNull() {
        RequestAddBookingDto requestAddBookingDto = BookingTestGenerator.getRequestAddBookingNullItemIdDto();
        Set<ConstraintViolation<RequestAddBookingDto>> availabilityViolations = validator.validate(requestAddBookingDto);
        assertEquals(1, availabilityViolations.size());
    }

    @Test
    void bookingAddStartShouldNotBeNull() {
        RequestAddBookingDto requestAddBookingDto = BookingTestGenerator.getRequestAddBookingNullStartIdDto();
        Set<ConstraintViolation<RequestAddBookingDto>> availabilityViolations = validator.validate(requestAddBookingDto);
        assertEquals(1, availabilityViolations.size());
    }

    @Test
    void bookingAddEndShouldNotBeNull() {
        RequestAddBookingDto requestAddBookingDto = BookingTestGenerator.getRequestAddBookingNullEndDto();
        Set<ConstraintViolation<RequestAddBookingDto>> availabilityViolations = validator.validate(requestAddBookingDto);
        assertEquals(1, availabilityViolations.size());
    }

    @Test
    void bookingAddEndShouldBeInFuture() {
        RequestAddBookingDto requestAddBookingDto = BookingTestGenerator.getRequestAddBookingPastEndDto();
        Set<ConstraintViolation<RequestAddBookingDto>> availabilityViolations = validator.validate(requestAddBookingDto);
        assertEquals(1, availabilityViolations.size());
    }

    @Test
    void bookingAddStartShouldBeInFuture() {
        RequestAddBookingDto requestAddBookingDto = BookingTestGenerator.getRequestAddBookingPastStartDto();
        Set<ConstraintViolation<RequestAddBookingDto>> availabilityViolations = validator.validate(requestAddBookingDto);
        assertEquals(1, availabilityViolations.size());
    }

    @Test
    void commentValidAdd() {
        RequestAddCommentDto requestAddCommentDto = CommentTestGenerator.getAddCommentDto();
        Set<ConstraintViolation<RequestAddCommentDto>> availabilityViolations = validator.validate(requestAddCommentDto);
        assertEquals(0, availabilityViolations.size());
    }

    @Test
    void commentAddTestShouldNotBeNull() {
        RequestAddCommentDto requestAddCommentDto = CommentTestGenerator.getAddCommentNullTextDto();
        Set<ConstraintViolation<RequestAddCommentDto>> availabilityViolations = validator.validate(requestAddCommentDto);
        assertEquals(1, availabilityViolations.size());
    }

    @Test
    void commentAddTestShouldNotBeEmpty() {
        RequestAddCommentDto requestAddCommentDto = CommentTestGenerator.getAddCommentEmptyTextDto();
        Set<ConstraintViolation<RequestAddCommentDto>> availabilityViolations = validator.validate(requestAddCommentDto);
        assertEquals(1, availabilityViolations.size());
    }
}
