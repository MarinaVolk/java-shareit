package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService service;

    @Autowired
    private MockMvc mvc;

    private static ItemRequest itemRequest1;
    private static ItemRequest itemRequest2;
    private static ItemRequestDto itemRequestDto1;

    @BeforeAll
    static void beforeAll() {
        itemRequest1 = new ItemRequest();
        itemRequest1.setDescription("itemRequest1");

        itemRequest2 = new ItemRequest();
        itemRequest2.setDescription("itemRequest2");

        itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setDescription("itemRequest1");
    }

    @Test
    void addItemRequestShouldAddRequest() throws Exception {
        when(service.addItemRequest(any(), anyLong()))
                .thenReturn(itemRequestDto1);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequest1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription())));
    }

    @Test
    void findItemRequestsByRequestorIdShouldProvideRequestByRequestorId() throws Exception {
        when(service.findItemRequestsByRequestorId(anyLong()))
                .thenReturn(List.of(itemRequestDto1));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()", is(1)));
    }

    @Test
    void findAllRequestsByPagesShouldProvideRequests() throws Exception {
        when(service.findAllRequestsByPages(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto1));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()", is(1)));
    }

    @Test
    void getItemRequestByIdShouldProvideRequestById() throws Exception {
        when(service.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestDto1);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequest1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription())));
    }
}