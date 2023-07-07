package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.booking.dto.RequestAddBookingDto;
import ru.practicum.shareit.booking.state.BookingState;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> save(long userId, RequestAddBookingDto requestAddBookingDto) {
        return post("", userId, requestAddBookingDto);
    }

    public ResponseEntity<Object> approve(long userId, Long bookingId, Boolean approved) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/{bookingId}")
                .queryParam("approved", approved)
                .buildAndExpand(bookingId);
        return patch(uriComponents.toUriString(), userId);
    }

    public ResponseEntity<Object> findById(long userId, long bookingId) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/{bookingId}")
                .buildAndExpand(bookingId);
        return get(uriComponents.toUriString(), userId);
    }

    public ResponseEntity<Object> findByUserIdAndState(
            long userId,
            BookingState state,
            Integer from,
            Integer size) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("")
                .queryParam("state", state)
                .queryParam("from", from)
                .queryParam("size", size)
                .build();
        return get(uriComponents.toUriString(), userId);
    }

    public ResponseEntity<Object> findByItemOwner(
            long userId,
            BookingState state,
            Integer from,
            Integer size) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/owner")
                .queryParam("state", state)
                .queryParam("from", from)
                .queryParam("size", size)
                .build();
        return get(uriComponents.toUriString(), userId);
    }
}
