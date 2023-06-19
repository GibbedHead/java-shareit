package ru.practicum.shareit.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.booking.*;
import ru.practicum.shareit.exception.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(final Exception e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(NotUniqueFieldException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleNotUniqueFieldException(final Exception e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(AccessNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleAccessException(final Exception e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(AccessForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(final Exception e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(AccessBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAccessBadRequestException(final Exception e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(BookingDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingDateException(final Exception e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(BookingItemNotAvailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingItemNotAvailableException(final Exception e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(BookingNotOwnerOperationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingNotOwnerOperationException(final Exception e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(BookingApprovingOwnBookingException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingApprovingOwnBookingException(final Exception e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(BookingOwnItemException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingOwnItemException(final Exception e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(BookingAlreadyApprovedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingAlreadyApprovedException(final Exception e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(BookingUnsupportedStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingUnsupportedStateException(final Exception e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors()
                .stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    private Map<String, Map<String, String>> getErrorsMap(Map<String, String> errors) {
        Map<String, Map<String, String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }
}
