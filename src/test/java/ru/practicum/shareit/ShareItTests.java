package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemTestGenerator;
import ru.practicum.shareit.user.dto.UserDto;
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
        UserDto validUser = UserTestGenerator.getUser();
        Set<ConstraintViolation<UserDto>> validViolations = validator.validate(validUser);
        assertEquals(validViolations.size(), 0);
    }

    @Test
    void requestUserNameShouldNotBeBlank() {
        UserDto nullNameUser = UserTestGenerator.getNullNameUser();
        Set<ConstraintViolation<UserDto>> nullViolations = validator.validate(nullNameUser);
        assertEquals(nullViolations.size(), 1);

        UserDto emptyNameUser = UserTestGenerator.getEmptyNameUser();
        Set<ConstraintViolation<UserDto>> emptyViolations = validator.validate(emptyNameUser);
        assertEquals(emptyViolations.size(), 1);
    }

    @Test
    void requestUserEmailShouldNotBeBlank() {
        UserDto nullEmailUser = UserTestGenerator.getNullEmailUser();
        Set<ConstraintViolation<UserDto>> nullViolations = validator.validate(nullEmailUser);
        assertEquals(nullViolations.size(), 1);

        UserDto emptyEmailUser = UserTestGenerator.getEmptyEmailUser();
        Set<ConstraintViolation<UserDto>> emptyViolations = validator.validate(emptyEmailUser);
        assertEquals(emptyViolations.size(), 1);
    }

    @Test
    void requestUserEmailShouldBeValidEmail() {
        UserDto invalidEmailUser = UserTestGenerator.getInvalidEmailUser();
        Set<ConstraintViolation<UserDto>> emailViolations = validator.validate(invalidEmailUser);
        assertEquals(emailViolations.size(), 1);
    }

    @Test
    void testValidItem() {
        ItemDto validItem = ItemTestGenerator.getItem();
        Set<ConstraintViolation<ItemDto>> validViolations = validator.validate(validItem);
        assertEquals(validViolations.size(), 0);
    }

    @Test
    void requestItemNameShouldNotBeBlank() {
        ItemDto nullNameItem = ItemTestGenerator.getNullNameItem();
        Set<ConstraintViolation<ItemDto>> nullViolations = validator.validate(nullNameItem);
        assertEquals(nullViolations.size(), 1);

        ItemDto emptyNameItem = ItemTestGenerator.getEmptyNameItem();
        Set<ConstraintViolation<ItemDto>> emptyViolations = validator.validate(emptyNameItem);
        assertEquals(emptyViolations.size(), 1);
    }

    @Test
    void requestItemDescriptionShouldNotBeBlank() {
        ItemDto nullDescriptionItem = ItemTestGenerator.getNullDescriptionIItem();
        Set<ConstraintViolation<ItemDto>> nullViolations = validator.validate(nullDescriptionItem);
        assertEquals(nullViolations.size(), 1);

        ItemDto emptyDescriptionItem = ItemTestGenerator.getEmptyDescriptionItem();
        Set<ConstraintViolation<ItemDto>> emptyViolations = validator.validate(emptyDescriptionItem);
        assertEquals(emptyViolations.size(), 1);
    }

    @Test
    void requestItemAvailabilityShouldBeNull() {
        ItemDto nullAvailabilityItem = ItemTestGenerator.getNullAvailabilityItem();
        Set<ConstraintViolation<ItemDto>> availabilityViolations = validator.validate(nullAvailabilityItem);
        assertEquals(availabilityViolations.size(), 1);
    }
}
