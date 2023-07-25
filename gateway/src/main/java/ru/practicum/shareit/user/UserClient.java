package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.RequestAddUserDto;
import ru.practicum.shareit.user.dto.RequestUpdateUserDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> save(RequestAddUserDto userDto) {
        return post("", userDto);
    }

    public ResponseEntity<Object> update(Long id, RequestUpdateUserDto userDto) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance().path("/{id}").buildAndExpand(id);
        return patch(uriComponents.toUriString(), userDto);
    }

    public ResponseEntity<Object> findById(Long id) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance().path("/{id}").buildAndExpand(id);
        return get(uriComponents.toUriString());
    }

    public ResponseEntity<Object> findAll() {
        return get("");
    }

    public void deleteById(Long id) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance().path("/{id}").buildAndExpand(id);
        delete(uriComponents.toUriString());
    }
}
