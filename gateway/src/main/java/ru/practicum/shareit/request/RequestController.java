package ru.practicum.shareit.request;/* # parse("File Header.java")*/

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * File Name: RequestController.java
 * Author: Marina Volkova
 * Date: 2023-09-21,   8:03 PM (UTC+3)
 * Description:
 */
@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                                 @RequestBody @Valid RequestGatewayDto requestGatewayDto) {

        return requestClient.addItemRequest(requestorId, requestGatewayDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long requestorId) {

        return requestClient.findAllRequestsByUser(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequestsByPages(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {

        return requestClient.findAllRequestsByPages(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findRequestById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                  @PathVariable Integer requestId) {

        return requestClient.findRequestById(userId, requestId);
    }
}
