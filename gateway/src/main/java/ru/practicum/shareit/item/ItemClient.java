package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.RequestAddCommentDto;
import ru.practicum.shareit.item.dto.RequestAddItemDto;
import ru.practicum.shareit.item.dto.RequestUpdateItemDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> save(Long userId, RequestAddItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> update(Long userId, Long id, RequestUpdateItemDto updateItemDto) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance().path("/{id}").buildAndExpand(id);
        return patch(uriComponents.toUriString(), userId, updateItemDto);
    }

    public ResponseEntity<Object> findById(Long userId, Long id) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance().path("/{id}").buildAndExpand(id);
        return get(uriComponents.toUriString(), userId);
    }

    public ResponseEntity<Object> findByUserId(Long userId, Integer from, Integer size) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("")
                .queryParam("from", from)
                .queryParam("size", size)
                .build();
        return get(uriComponents.toUriString(), userId);
    }

    public void deleteById(Long id) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance().path("/{id}").buildAndExpand(id);
        delete(uriComponents.toUriString());
    }

    public ResponseEntity<Object> findByNameOrDescription(String text, Integer from, Integer size) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/search")
                .queryParam("text", text)
                .queryParam("from", from)
                .queryParam("size", size)
                .build();
        return get(uriComponents.toUriString());
    }

    public ResponseEntity<Object> saveComment(Long userId, Long id, RequestAddCommentDto addCommentDto) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/{id}/comment")
                .buildAndExpand(id);
        return post(uriComponents.toUriString(), userId, addCommentDto);
    }
}
