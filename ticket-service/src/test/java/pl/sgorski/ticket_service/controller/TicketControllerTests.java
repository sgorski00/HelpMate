package pl.sgorski.ticket_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.sgorski.common.exception.NotCompatibleRoleException;
import pl.sgorski.common.exception.TicketNotFoundException;
import pl.sgorski.common.exception.UserNotFoundException;
import pl.sgorski.common.dto.TicketDto;
import pl.sgorski.ticket_service.mapper.TicketMapper;
import pl.sgorski.ticket_service.model.Ticket;
import pl.sgorski.ticket_service.model.TicketStatus;
import pl.sgorski.ticket_service.service.TicketService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TicketControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketService ticketService;

    @MockitoBean
    private TicketMapper ticketMapper;

    private TicketDto responseDto;
    private String userId;

    @BeforeEach
    void setUp() {
        responseDto = new TicketDto(
                1L,
                "Test Ticket",
                "This is a test ticket.",
                "Opened",
                UUID.randomUUID(),
                UUID.randomUUID(),
                Timestamp.from(Instant.now()),
                Timestamp.from(Instant.now())
        );
        userId = UUID.randomUUID().toString();
    }

    @Test
    void shouldCreateTicket() throws Exception {
        when(ticketService.createTicket(any(), any())).thenReturn(new Ticket());
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/tickets")
                        .contentType("application/json")
                        .content("{\"title\":\"Test Ticket\",\"description\":\"This is a test ticket.\"}")
                        .with(jwt().jwt(jwt -> jwt.subject(userId)
                                .claim("realm_access", Map.of("roles", List.of("USER"))))))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    assertTrue(result.getResponse().getContentAsString().contains("Test Ticket"));
                    assertTrue(result.getResponse().getContentAsString().contains("This is a test ticket."));
        });
        verify(ticketService, times(1)).createTicket(any(), any());
    }

    @Test
    void shouldNotCreateTicket_NotValidData_BlankTitle() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType("application/json")
                        .content("{\"title\":\"\",\"description\":\"This is a test ticket.\"}")
                        .with(jwt().jwt(jwt -> jwt.subject(userId)
                                .claim("realm_access", Map.of("roles", List.of("USER"))))))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("error")));
        verify(ticketService, never()).createTicket(any(), any());
    }

    @Test
    void shouldNotCreateTicket_UserNotFound() throws Exception {
        when(ticketService.createTicket(any(), any())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(post("/api/tickets")
                        .contentType("application/json")
                        .content("{\"title\":\"Test Ticket\",\"description\":\"This is a test ticket.\"}")
                        .with(jwt().jwt(jwt -> jwt.subject(userId)
                                .claim("realm_access", Map.of("roles", List.of("ADMIN"))))))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("User not found")));
        verify(ticketService, times(1)).createTicket(any(), any());
    }

    @Test
    void shouldAssignTechnicianToTheTicket() throws Exception {
        when(ticketService.assignTicketById(any(), any())).thenReturn(new Ticket());
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(responseDto);

        String techId = UUID.randomUUID().toString();
        mockMvc.perform(put("/api/tickets/1/assign")
                        .param("assignee", techId)
                        .with(jwt().jwt(jwt -> jwt.subject(userId)
                                .claim("realm_access", Map.of("roles", List.of("ADMIN"))))))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    assertTrue(result.getResponse().getContentAsString().contains("Test Ticket"));
                    assertTrue(result.getResponse().getContentAsString().contains("This is a test ticket."));
                });

        verify(ticketService, times(1)).assignTicketById(anyLong(), any(UUID.class));
    }

    @Test
    void shouldNotAssignTechnicianToTheTicket_TicketNotFound() throws Exception {
        when(ticketService.assignTicketById(any(), any())).thenThrow(new TicketNotFoundException("Ticket not found"));

        String techId = UUID.randomUUID().toString();
        mockMvc.perform(put("/api/tickets/1/assign")
                        .param("assignee", techId)
                        .with(jwt().jwt(jwt -> jwt.subject(userId)
                                .claim("realm_access", Map.of("roles", List.of("ADMIN"))))))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Ticket not found")));
        verify(ticketService, times(1)).assignTicketById(anyLong(), any(UUID.class));
    }

    @Test
    void shouldNotAssignUserToTheTicket_NotATechnician() throws Exception {
        when(ticketService.assignTicketById(any(), any())).thenThrow(new NotCompatibleRoleException("User's role is not sufficient"));

        String notTechId = UUID.randomUUID().toString();
        mockMvc.perform(put("/api/tickets/1/assign")
                        .param("assignee", notTechId)
                        .with(jwt().jwt(jwt -> jwt.subject(userId)
                                .claim("realm_access", Map.of("roles", List.of("ADMIN"))))))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("User's role is not sufficient")));
        verify(ticketService, times(1)).assignTicketById(anyLong(), any(UUID.class));
    }

    @Test
    void shouldChangeTicketStatus() throws Exception {
        when(ticketService.changeStatusById(any(), any())).thenReturn(new Ticket());
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/tickets/1/status")
                        .param("status", "IN_PROGRESS")
                        .with(jwt().jwt(jwt -> jwt.subject(userId)
                                .claim("realm_access", Map.of("roles", List.of("ADMIN"))))))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    assertTrue(result.getResponse().getContentAsString().contains("Test Ticket"));
                    assertTrue(result.getResponse().getContentAsString().contains("This is a test ticket."));
                });
        verify(ticketService, times(1)).changeStatusById(1L, TicketStatus.IN_PROGRESS);
    }

    @Test
    void shouldNotChangeTicketStatus_TicketNotFound() throws Exception {
        when(ticketService.changeStatusById(any(), any())).thenThrow(new TicketNotFoundException("Ticket not found"));

        mockMvc.perform(put("/api/tickets/1/status")
                        .param("status", "IN_PROGRESS")
                        .with(jwt().jwt(jwt -> jwt.subject(userId)
                                .claim("realm_access", Map.of("roles", List.of("ADMIN"))))))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Ticket not found")));
        verify(ticketService, times(1)).changeStatusById(1L, TicketStatus.IN_PROGRESS);
    }

    @Test
    void shouldUpdateTicket() throws Exception {
        when(ticketService.updateTicketById(any(), any())).thenReturn(new Ticket());
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/tickets/1")
                        .contentType("application/json")
                        .content("{\"title\":\"Test Ticket\",\"description\":\"This is a test ticket.\"}")
                        .with(jwt().jwt(jwt -> jwt.subject(userId)
                                .claim("realm_access", Map.of("roles", List.of("ADMIN"))))))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    assertTrue(result.getResponse().getContentAsString().contains("Test Ticket"));
                    assertTrue(result.getResponse().getContentAsString().contains("This is a test ticket."));
                });
        verify(ticketService, times(1)).updateTicketById(anyLong(), any());
    }

    @Test
    void shouldNotUpdateTicket_NotValidData_BlankTitle() throws Exception {
        mockMvc.perform(put("/api/tickets/1")
                        .contentType("application/json")
                        .content("{\"title\":\"\",\"description\":\"This is a test ticket.\"}")
                        .with(jwt().jwt(jwt -> jwt.subject(userId)
                                .claim("realm_access", Map.of("roles", List.of("ADMIN"))))))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("error")));
        verify(ticketService, never()).updateTicketById(anyLong(), any());
    }

    @Test
    void shouldNotUpdateTicket_UserNotFound() throws Exception {
        when(ticketService.updateTicketById(any(), any())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(put("/api/tickets/1")
                        .contentType("application/json")
                        .content("{\"title\":\"Test Ticket\",\"description\":\"This is a test ticket.\"}")
                        .with(jwt().jwt(jwt -> jwt.subject(userId)
                                .claim("realm_access", Map.of("roles", List.of("ADMIN"))))))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("User not found")));
        verify(ticketService, times(1)).updateTicketById(anyLong(), any());
    }

    @Test
    void shouldReturnTicketById() throws Exception {
        when(ticketService.getTicketById(any())).thenReturn(new Ticket());
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(responseDto);

        mockMvc.perform(get("/api/tickets/1")
                        .with(jwt().jwt(jwt -> jwt.subject(userId)
                                .claim("realm_access", Map.of("roles", List.of("ADMIN"))))))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    assertTrue(result.getResponse().getContentAsString().contains("Test Ticket"));
                    assertTrue(result.getResponse().getContentAsString().contains("This is a test ticket."));
                });
        verify(ticketService, times(1)).getTicketById(anyLong());
    }

    @Test
    void shouldNotReturnTicketById_TicketNotFound() throws Exception {
        when(ticketService.getTicketById(any())).thenThrow(new TicketNotFoundException("Ticket not found"));
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(responseDto);

        mockMvc.perform(get("/api/tickets/1")
                        .with(jwt().jwt(jwt -> jwt.subject(userId)
                                .claim("realm_access", Map.of("roles", List.of("ADMIN"))))))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Ticket not found")));
        verify(ticketService, times(1)).getTicketById(anyLong());
    }

    @Test
    void shouldReturnAllTickets_Admin() throws Exception {
        when(ticketService.getTicketsForCurrentUser(any(), any())).thenReturn(
                new PageImpl<>(List.of(new Ticket()))
        );
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(responseDto);

        mockMvc.perform(get("/api/tickets")
                        .param("page", "1")
                        .param("size", "10")
                        .with(jwt().jwt(jwt -> jwt.subject(userId)
                                .claim("realm_access", Map.of("roles", List.of("ADMIN"))))))
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Test Ticket")));
        verify(ticketService, times(1)).getTicketsForCurrentUser(any(), any());
    }

    @Test
    void shouldReturnTickets_User() throws Exception {
        when(ticketService.getTicketsForCurrentUser(any(), any())).thenReturn(
                new PageImpl<>(List.of(new Ticket()))
        );
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(responseDto);

        mockMvc.perform(get("/api/tickets")
                        .param("page", "2")
                        .param("size", "5")
                        .with(jwt().jwt(jwt -> jwt.subject(userId)
                                .claim("realm_access", Map.of("roles", List.of("USER"))))))
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Test Ticket")));
        verify(ticketService, times(1)).getTicketsForCurrentUser(any(), any());
    }

    @Test
    void shouldNotReturnTickets_UserNotFound() throws Exception {
        when(ticketService.getTicketsForCurrentUser(any(), any())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/api/tickets")
                        .param("page", "1")
                        .param("size", "10")
                        .with(jwt().jwt(jwt -> jwt.subject(userId)
                                .claim("realm_access", Map.of("roles", List.of("USER"))))))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("User not found")));
        verify(ticketService, times(1)).getTicketsForCurrentUser(any(), any());
    }
}
