package pl.sgorski.ticket_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.sgorski.ticket_service.config.properties.RabbitTicketExchangeProperties;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class RabbitProducerConfig {

    private final RabbitTicketExchangeProperties rabbitProperties;

    @Bean
    public TopicExchange ticketExchange() {
        return new TopicExchange(rabbitProperties.exchangeName(), true, false);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory cf, Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(cf);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
        rabbitTemplate.setReturnsCallback(r -> log.warn("Message returned from exchange: {}, routingKey: {}", r.getExchange(), r.getRoutingKey()));
        return rabbitTemplate;
    }
}
