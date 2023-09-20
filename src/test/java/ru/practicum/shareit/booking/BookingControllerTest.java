package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemResponseShortDto;
import ru.practicum.shareit.user.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private static BookingDto bookingDto1;
    private static UserDto userDto;
    private static ItemResponseShortDto item;

    @BeforeAll
    static void beforeAll() {

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("user1");
        userDto.setEmail("user1@email");

        item = new ItemResponseShortDto(1L, "item1");

        bookingDto1 = new BookingDto();
        bookingDto1.setStatus(BookingStatus.WAITING);
        bookingDto1.setId(1L);
        bookingDto1.setBooker(userDto);
        bookingDto1.setItemId(1L);
        bookingDto1.setItem(item);
        bookingDto1.setStart(LocalDateTime.now().plusDays(1));
        bookingDto1.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void addBookingShouldAddBooking() throws Exception {
        when(bookingService.addBooking(any(), anyLong()))
                .thenReturn(bookingDto1);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void approveBookingShouldApproveBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto1);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void getBookingByIdAndBookerIdShouldProvideBooking() throws Exception {
        when(bookingService.getBookingByIdAndBookerId(anyLong(), anyLong()))
                .thenReturn(bookingDto1);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void getAllBookingsByBookerIdDescShouldProvideAllBookingsByBookerId() throws Exception {
        when(bookingService.getAllBookingsByBookerIdDesc(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto1));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()", is(1)));
    }

    @Test
    void getAllBookingsByItemOwnerIdShouldProvideBookings() throws Exception {
        when(bookingService.getAllBookingsByItemOwnerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto1));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()", is(1)));
    }
}