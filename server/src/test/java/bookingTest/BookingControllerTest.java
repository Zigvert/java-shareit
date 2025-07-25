package bookingTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ShareItApp.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ru.practicum.shareit.booking.service.BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBooking_returnsBookingResponseDto() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponseDto responseDto = new BookingResponseDto(
                1L,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                BookingStatus.WAITING,
                new UserShortDto(2L),
                new ItemShortDto(1L, "Drill")
        );

        Mockito.when(bookingService.create(anyLong(), any(BookingDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.booker.id").value(2))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Drill"));
    }

    @Test
    void updateBooking_returnsUpdatedBookingResponseDto() throws Exception {
        BookingResponseDto updatedResponse = new BookingResponseDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.APPROVED,
                new UserShortDto(2L),
                new ItemShortDto(1L, "Drill")
        );

        Mockito.when(bookingService.update(eq(2L), eq(1L), eq(true))).thenReturn(updatedResponse);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 2L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getById_returnsBookingResponseDto() throws Exception {
        BookingResponseDto responseDto = new BookingResponseDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING,
                new UserShortDto(2L),
                new ItemShortDto(1L, "Drill")
        );

        Mockito.when(bookingService.getById(2L, 1L)).thenReturn(responseDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.booker.id").value(2));
    }

    @Test
    void getAllByBooker_returnsListOfBookingResponseDtos() throws Exception {
        BookingResponseDto responseDto = new BookingResponseDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING,
                new UserShortDto(2L),
                new ItemShortDto(1L, "Drill")
        );

        Mockito.when(bookingService.getAllByBooker(eq(2L), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].booker.id").value(2));
    }

    @Test
    void getAllByOwner_returnsListOfBookingResponseDtos() throws Exception {
        BookingResponseDto responseDto = new BookingResponseDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING,
                new UserShortDto(2L),
                new ItemShortDto(1L, "Drill")
        );

        Mockito.when(bookingService.getAllByOwner(eq(2L), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 2L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].booker.id").value(2));
    }
}
