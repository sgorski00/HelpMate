package pl.sgorski.ticket_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.sgorski.common.dto.TicketDto;
import pl.sgorski.common.exception.TicketNotFoundException;
import pl.sgorski.ticket_service.config.SecurityConfig;
import pl.sgorski.ticket_service.mapper.TicketMapper;
import pl.sgorski.ticket_service.model.Ticket;
import pl.sgorski.ticket_service.service.TicketService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InternalController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InternalControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketService ticketService;

    @MockitoBean
    private TicketMapper ticketMapper;

    @Test
    @WithMockUser(roles = "SERVICE_COMMENT")
    void shouldGetTicketById_Status200() throws Exception {
        var ticketId = 1L;
        var ticket = mock(Ticket.class);
        var ticketDto = mock(TicketDto.class);

        when(ticketService.getTicketById(ticketId)).thenReturn(ticket);
        when(ticketMapper.toDto(ticket)).thenReturn(ticketDto);

        mockMvc.perform(get("/api/internal/tickets/{ticketId}", ticketId))
                .andExpect(status().isOk());
        verify(ticketService).getTicketById(ticketId);
        verify(ticketMapper).toDto(ticket);
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetTicketById_Status403() throws Exception {
        mockMvc.perform(get("/api/internal/tickets/{ticketId}", 1L))
                .andExpect(status().isForbidden());
        verify(ticketService, never()).getTicketById(anyLong());
    }

    @Test
    @WithMockUser(roles = "SERVICE_COMMENT")
    void shouldGetTicketById_Status404() throws Exception {
        var ticketId = 99L;
        when(ticketService.getTicketById(ticketId)).thenThrow(new TicketNotFoundException("Not found"));

        mockMvc.perform(get("/api/internal/tickets/{ticketId}", ticketId))
                .andExpect(status().isNotFound());
        verify(ticketService).getTicketById(ticketId);
    }

    @Test
    @WithMockUser(roles = "SERVICE_COMMENT")
    void shouldCheckIfUserIsTicketCreator_Status200() throws Exception {
        var ticketId = 1L;
        var userId = UUID.randomUUID();
        when(ticketService.isTicketCreator(ticketId, userId)).thenReturn(true);

        mockMvc.perform(get("/api/internal/tickets/{ticketId}/is-creator/{userId}", ticketId, userId))
                .andExpect(status().isOk());
        verify(ticketService).isTicketCreator(ticketId, userId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldCheckIfUserIsTicketCreator_Status403() throws Exception {
        mockMvc.perform(get("/api/internal/tickets/{ticketId}/is-creator/{userId}", 1L, UUID.randomUUID()))
                .andExpect(status().isForbidden());
        verifyNoInteractions(ticketService);
    }

    @Test
    @WithMockUser(roles = "SERVICE_COMMENT")
    void shouldCheckIfUserIsTicketCreator_Status404() throws Exception {
        var ticketId = 99L;
        var userId = UUID.randomUUID();
        when(ticketService.isTicketCreator(ticketId, userId)).thenThrow(new TicketNotFoundException("Not found"));

        mockMvc.perform(get("/api/internal/tickets/{ticketId}/is-creator/{userId}", ticketId, userId))
                .andExpect(status().isNotFound());
        verify(ticketService).isTicketCreator(ticketId, userId);
    }
}
