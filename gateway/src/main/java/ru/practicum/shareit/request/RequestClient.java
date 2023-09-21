package ru.practicum.shareit.request;/* # parse("File Header.java")*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;
/**
 * File Name: RequestClient.java
 * Author: Marina Volkova
 * Date: 2023-09-21,   8:01 PM (UTC+3)
 * Description:
 */
@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> addItemRequest(Long requestorId, RequestGatewayDto itemRequestGatewayDto) {
        return post("", requestorId, itemRequestGatewayDto);
    }

    public ResponseEntity<Object> findAllRequestsByUser(Long requestorId) {
        return get("", requestorId);
    }

    public ResponseEntity<Object> findAllRequestsByPages(Long requestorId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", requestorId, parameters);
    }

    public ResponseEntity<Object> findRequestById(Long requestorId, Integer requestId) {
        return get("/" + requestId, requestorId);
    }

}
