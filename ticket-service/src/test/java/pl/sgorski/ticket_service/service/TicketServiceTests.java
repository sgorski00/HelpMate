package pl.sgorski.ticket_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import pl.sgorski.common.dto.UserDto;
import pl.sgorski.common.exception.IllegalStatusChangeException;
import pl.sgorski.common.exception.NotCompatibleRoleException;
import pl.sgorski.common.exception.TicketNotFoundException;
import pl.sgorski.common.exception.UserNotFoundException;
import pl.sgorski.common.utils.AuthorityUtils;
import pl.sgorski.ticket_service.dto.CreateTicketRequest;
import pl.sgorski.ticket_service.dto.UpdateTicketRequest;
import pl.sgorski.ticket_service.mapper.TicketMapper;
import pl.sgorski.ticket_service.model.Ticket;
import pl.sgorski.ticket_service.model.TicketStatus;
import pl.sgorski.ticket_service.repository.TicketRepository;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTests {

    @Mock
    private UserClientService userClientService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketMapper mapper;

    @InjectMocks
    private TicketService ticketService;

    private UserDto userDto;
    private UserDto userTechDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(
            "testuserID",
            "testuser",
            "testemail",
            "John",
            "Doe",
            Set.of("USER")
        );

        userTechDto = new UserDto(
                "testuserID",
                "testuser",
                "testemail",
                "John",
                "Doe",
                Set.of("TECHNICIAN")
        );
    }

    @Test
    void shouldCreateTicket() {
        when(mapper.toTicket(any())).thenReturn(new Ticket());
        when(userClientService.getUserById(any())).thenReturn(Mono.just(userDto));

        ticketService.createTicket(new CreateTicketRequest("test", "test"), "repID");

        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void shouldNotCreateTicket_ReporterNotFound() {
        when(mapper.toTicket(any())).thenReturn(new Ticket());
        when(userClientService.getUserById(any())).thenReturn(Mono.empty());

        assertThrows(UserNotFoundException.class, () -> ticketService.createTicket(new CreateTicketRequest("test", "test"), "repID"));

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void shouldGetTicketById() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(new Ticket()));

        Ticket ticket = ticketService.getTicketById(1L);

        assertNotNull(ticket);
        verify(ticketRepository, times(1)).findById(1L);
    }

    @Test
    void shouldNotGetTicketById_TicketNotFound() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () -> ticketService.getTicketById(1L));

        verify(ticketRepository, times(1)).findById(1L);
    }

    @Test
    void shouldGetTicketsForCurrentUser_Admin() {
        try(MockedStatic<AuthorityUtils> mockAuthUtils = mockStatic(AuthorityUtils.class)) {
            mockAuthUtils.when(() -> AuthorityUtils.isAdmin(any())).thenReturn(true);
            when(ticketRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

            var result = ticketService.getTicketsForCurrentUser(mock(Authentication.class), PageRequest.of(0, 10));

            assertNotNull(result);
            verify(ticketRepository, times(1)).findAll(any(Pageable.class));
            verify(userClientService, never()).getUserById(anyString());
            verify(ticketRepository, never()).findAllByReporterId(anyString(), any(Pageable.class));
        }
    }

    @Test
    void shouldGetTicketsForCurrentUser_Technician() {
        try(MockedStatic<AuthorityUtils> mockAuthUtils = mockStatic(AuthorityUtils.class)) {
            mockAuthUtils.when(() -> AuthorityUtils.isAdmin(any())).thenReturn(false);
            mockAuthUtils.when(() -> AuthorityUtils.isTechnician(any())).thenReturn(true);
            when(ticketRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

            var result = ticketService.getTicketsForCurrentUser(mock(Authentication.class), PageRequest.of(0, 10));

            assertNotNull(result);
            verify(ticketRepository, times(1)).findAll(any(Pageable.class));
            verify(userClientService, never()).getUserById(anyString());
            verify(ticketRepository, never()).findAllByReporterId(anyString(), any(Pageable.class));
        }
    }

    @Test
    void shouldNotGetTicketsForCurrentUser_AdminAndTechnician() {
        try(MockedStatic<AuthorityUtils> mockAuthUtils = mockStatic(AuthorityUtils.class)) {
            mockAuthUtils.when(() -> AuthorityUtils.isAdmin(any())).thenReturn(true);
            mockAuthUtils.when(() -> AuthorityUtils.isTechnician(any())).thenReturn(true);
            when(ticketRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

            var result = ticketService.getTicketsForCurrentUser(mock(Authentication.class), PageRequest.of(0, 10));

            assertNotNull(result);
            verify(ticketRepository, times(1)).findAll(any(Pageable.class));
            verify(userClientService, never()).getUserById(anyString());
            verify(ticketRepository, never()).findAllByReporterId(anyString(), any(Pageable.class));
        }
    }

    @Test
    void shouldGetTicketsForCurrentUser_Reporter() {
        try(MockedStatic<AuthorityUtils> mockAuthUtils = mockStatic(AuthorityUtils.class)) {
            mockAuthUtils.when(() -> AuthorityUtils.isAdmin(any())).thenReturn(false);
            mockAuthUtils.when(() -> AuthorityUtils.isTechnician(any())).thenReturn(false);
            when(userClientService.getUserById(nullable(String.class))).thenReturn(Mono.just(userDto));
            when(ticketRepository.findAllByReporterId(anyString(),any(Pageable.class))).thenReturn(Page.empty());

            var result = ticketService.getTicketsForCurrentUser(mock(Authentication.class), PageRequest.of(0, 10));

            assertNotNull(result);
            verify(ticketRepository, never()).findAll(any(Pageable.class));
            verify(userClientService, times(1)).getUserById(nullable(String.class));
            verify(ticketRepository, times(1)).findAllByReporterId(anyString(), any(Pageable.class));
        }
    }

    @Test
    void shouldNotGetTicketsForCurrentUser_UserNotFound() {
        try(MockedStatic<AuthorityUtils> mockAuthUtils = mockStatic(AuthorityUtils.class)) {
            mockAuthUtils.when(() -> AuthorityUtils.isAdmin(any())).thenReturn(false);
            mockAuthUtils.when(() -> AuthorityUtils.isTechnician(any())).thenReturn(false);
            when(userClientService.getUserById(nullable(String.class))).thenReturn(Mono.empty());

            assertThrows(UserNotFoundException.class, () -> ticketService.getTicketsForCurrentUser(mock(Authentication.class), PageRequest.of(0, 10)));

            verify(ticketRepository, never()).findAll(any(Pageable.class));
            verify(userClientService, times(1)).getUserById(nullable(String.class));
            verify(ticketRepository, never()).findAllByReporterId(anyString(), any(Pageable.class));
        }
    }

    @Test
    void shouldAssignTicketById() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(new Ticket()));
        when(userClientService.getUserById(anyString())).thenReturn(Mono.just(userTechDto));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(new Ticket());

        Ticket res = ticketService.assignTicketById(1L, "techID");

        assertNotNull(res);
        verify(ticketRepository, times(1)).findById(anyLong());
        verify(userClientService, times(1)).getUserById(anyString());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void shouldNotAssignTicketById_UserNotFound() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(new Ticket()));
        when(userClientService.getUserById(anyString())).thenReturn(Mono.empty());

        assertThrows(UserNotFoundException.class, () -> ticketService.assignTicketById(1L, "techID"));

        verify(ticketRepository, times(1)).findById(anyLong());
        verify(userClientService, times(1)).getUserById(anyString());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void shouldNotAssignTicketById_NotTechnician() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(new Ticket()));
        when(userClientService.getUserById(anyString())).thenReturn(Mono.just(userDto));

        assertThrows(NotCompatibleRoleException.class, () -> ticketService.assignTicketById(1L, "notTechID"));

        verify(ticketRepository, times(1)).findById(anyLong());
        verify(userClientService, times(1)).getUserById(anyString());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void shouldNotAssignTicketById_TicketNotFound() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () -> ticketService.assignTicketById(1L, "notTechID"));

        verify(ticketRepository, times(1)).findById(anyLong());
        verify(userClientService, never()).getUserById(anyString());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void shouldNotAssignTicketById_TicketNotModifiable() {
        Ticket mockTicket = mock(Ticket.class);
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(mockTicket));
        doThrow(new IllegalStatusChangeException("Status cannot be changed")).when(mockTicket).setStatus(any());
        when(userClientService.getUserById(anyString())).thenReturn(Mono.just(userTechDto));

        assertThrows(IllegalStatusChangeException.class, () -> ticketService.assignTicketById(1L, "techID"));

        verify(ticketRepository, times(1)).findById(anyLong());
        verify(userClientService, times(1)).getUserById(anyString());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void shouldUpdateTicket() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(new Ticket()));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(new Ticket());

        Ticket res = ticketService.updateTicketById(1L, new UpdateTicketRequest("title", "description"));

        assertNotNull(res);
        verify(ticketRepository, times(1)).findById(anyLong());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void shouldNotUpdateTicket_TicketNotFound() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () -> ticketService.updateTicketById(1L, new UpdateTicketRequest("title", "description")));

        verify(ticketRepository, times(1)).findById(anyLong());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void shouldNotUpdateTicket_TicketNotModifiable() {
        Ticket mockTicket = mock(Ticket.class);
        doThrow(IllegalStatusChangeException.class).when(mockTicket).update(any());
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(mockTicket));

        assertThrows(IllegalStatusChangeException.class, () -> ticketService.updateTicketById(1L, new UpdateTicketRequest("title", "description")));

        verify(ticketRepository, times(1)).findById(anyLong());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void shouldChangeTicketStatus() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(new Ticket()));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(new Ticket());

        Ticket res = ticketService.changeStatusById(1L, TicketStatus.RESOLVED);

        assertNotNull(res);
        verify(ticketRepository, times(1)).findById(anyLong());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void shouldNotChangeTicketStatus_TicketNotFound() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () -> ticketService.changeStatusById(1L, TicketStatus.REJECTED));

        verify(ticketRepository, times(1)).findById(anyLong());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void shouldNotChangeTicketStatus_TicketNotModifiable() {
        Ticket mockTicket = mock(Ticket.class);
        doThrow(IllegalStatusChangeException.class).when(mockTicket).setStatus(any());
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(mockTicket));

        assertThrows(IllegalStatusChangeException.class, () -> ticketService.changeStatusById(1L, TicketStatus.RESOLVED));

        verify(ticketRepository, times(1)).findById(anyLong());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }
}
