package pl.sgorski.common.event;

public interface TicketEvent extends Event{
    String ticketId();
    String title();
    String description();
}
