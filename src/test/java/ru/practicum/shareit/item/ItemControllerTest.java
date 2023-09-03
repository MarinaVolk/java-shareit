package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.comments.Comment;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;
    private static Item item1;
    private static User user1;
    private static ItemDto itemDto1;
    private static Comment comment1;
    private static CommentDto commentDto1;
    private static ItemResponseFullDto itemResponseFullDto1;

    @BeforeAll
    static void beforeAll() {

        user1 = new User();
        user1.setId(1L);
        user1.setName("user1");
        user1.setEmail("user1@email.com");

        item1 = new Item();
        item1.setId(1L);
        item1.setName("item");
        item1.setDescription("itemDescription");
        item1.setAvailable(true);
        item1.setOwner(user1);
        item1.setRequestId(1L);

        itemDto1 = new ItemDto();
        itemDto1.setId(1L);
        itemDto1.setName("item");
        itemDto1.setDescription("itemDescription");
        itemDto1.setAvailable(true);
        itemDto1.setOwnerId(1L);
        itemDto1.setRequestId(1L);

        comment1 = new Comment();
        comment1.setId(1L);
        comment1.setText("comment");
        comment1.setItem(item1);
        comment1.setAuthor(user1);
        comment1.setCreated(LocalDateTime.now());

        itemResponseFullDto1 = new ItemResponseFullDto();
        itemResponseFullDto1.setId(1L);
        itemResponseFullDto1.setName("item");
        itemResponseFullDto1.setDescription("itemDescription");
        itemResponseFullDto1.setAvailable(true);

        commentDto1 = new CommentDto();
        commentDto1.setText("comment1text");
        commentDto1.setId(1L);
        commentDto1.setAuthorId(1L);
        commentDto1.setCreated(LocalDateTime.now());
    }

    @Test
    void addItemShouldAddItem() throws Exception {
        when(itemService.addItem(anyLong(), any()))
                .thenReturn(itemDto1);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())));
    }

    @Test
    void updateItemShouldUpdateItem() throws Exception {
        when(itemService.updateItem(anyLong(), any(), anyLong()))
                .thenReturn(itemDto1);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())));
    }

    @Test
    void getItemsListByOwnerIdShouldProvideItemsListByOwnerId() throws Exception {
        when(itemService.getItemsListByOwnerId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(item1));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "20")
                        .content(mapper.writeValueAsString(item1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()", is(1)));
    }

    @Test
    void searchItemByTextShouldProvideItemByText() throws Exception {
        when(itemService.searchItemByText(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto1));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "textForSearch")
                        .param("from", "0")
                        .param("size", "20")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()", is(1)));
    }
}