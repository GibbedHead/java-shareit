package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.RequestAddItemRequestDto;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> save(long userId, RequestAddItemRequestDto requestAddItemRequestDto) {
        return post("", userId, requestAddItemRequestDto);
    }

    public ResponseEntity<Object> findByUserId(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> findAllNotOwned(long userId, Integer from, Integer size) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/all")
                .queryParam("from", from)
                .queryParam("size", size)
                .build();
        return get(uriComponents.toUriString(), userId);
    }

    public ResponseEntity<Object> findById(long userId, Long id) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/{id}")
                .buildAndExpand(id);
        return get(uriComponents.toUriString(), userId);
    }
}
