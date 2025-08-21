package pl.sgorski.ticket_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.sgorski.ticket_service.dto.CreateTicketRequest;
import pl.sgorski.ticket_service.dto.TicketEntityResponse;
import pl.sgorski.ticket_service.model.Ticket;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "reporterId", ignore = true)
    @Mapping(target = "assigneeId", ignore = true)
    Ticket toTicket(CreateTicketRequest request);

    @Mapping(target = "status", source = "status.displayName")
    TicketEntityResponse toDto(Ticket ticket);
}
