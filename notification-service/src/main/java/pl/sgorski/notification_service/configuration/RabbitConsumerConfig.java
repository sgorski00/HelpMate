package pl.sgorski.notification_service.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.sgorski.notification_service.configuration.properties.RabbitTicketExchangeProperties;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RabbitTicketExchangeProperties.class)
@EnableRabbit
public class RabbitConsumerConfig {

    private final RabbitTicketExchangeProperties rabbitTicketExchangeProperties;

    @Bean
    public TopicExchange ticketExchange() {
        return new TopicExchange(rabbitTicketExchangeProperties.exchangeName(), true, false);
    }

    @Bean
    public Queue ticketCreatedQueue() {
        return new Queue(rabbitTicketExchangeProperties.createdQueue(), true);
    }

    @Bean
    public Binding ticketCreatedBinding() {
        return BindingBuilder
                .bind(ticketCreatedQueue())
                .to(ticketExchange())
                .with(rabbitTicketExchangeProperties.createdRoutingKey());
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
